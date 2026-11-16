import { formatBytes, formatTimestampRange } from '#/lib/components/format';
import { options } from './options.svelte';

export interface TranscriptionSegment {
  start: number;
  end: number;
  text: string;
}

interface TranscriptionRequest {
  file: File;
  prompt?: string;
  stream: boolean;
  language?: string;
  translate?: boolean;
  temperature?: number;
  temperatureInc?: number;
  offsetTMs?: number;
  offsetN?: number;
  durationMs?: number;
  maxContext?: number;
  maxLen?: number;
  splitOnWord?: boolean;
  bestOf?: number;
  beamSize?: number;
  audioContext?: number;
  wordThreshold?: number;
  entropyThreshold?: number;
  logprobThreshold?: number;
}

export interface SubmittedRun {
  fileName: string;
  fileSize: string;
  prompt?: string;
}

class TranscriptionState {
  file = $state<File | null>(null);
  prompt = $state('');
  isDragging = $state(false);
  isLoading = $state(false);
  segments = $state<TranscriptionSegment[]>([]);
  error = $state<string | null>(null);
  submitted = $state<SubmittedRun | null>(null);

  get text(): string {
    return this.segments.map((segment) => segment.text.trim()).join(' ');
  }

  setFile(file: File) {
    this.file = file;
    this.error = null;
  }

  clearFile() {
    this.file = null;
  }

  reset() {
    this.file = null;
    this.prompt = '';
    this.segments = [];
    this.error = null;
    this.submitted = null;
  }

  async submit() {
    const file = this.file;
    if (!file || this.isLoading) return;

    const prompt = this.prompt.trim();
    this.isLoading = true;
    this.segments = [];
    this.error = null;
    this.submitted = { fileName: file.name, fileSize: formatBytes(file.size), prompt };
    this.isDragging = false;

    try {
      await streamTranscription(
        {
          file,
          prompt: prompt || undefined,
          stream: true,
          ...$state.snapshot(options.settings)
        },
        (segment) => {
          this.segments = [...this.segments, segment];
        }
      );
      this.file = null;
    } catch (e) {
      this.error = e instanceof Error ? e.message : 'An error occurred';
    } finally {
      this.isLoading = false;
    }
  }

  exportText(): string {
    if (!options.settings.includeTimestamps) return this.text.trim();
    return this.segments
      .map(
        (segment) => `${formatTimestampRange(segment.start, segment.end)} ${segment.text.trim()}`
      )
      .join('\n');
  }

  async copy() {
    if (!this.text) return;
    await navigator.clipboard.writeText(this.exportText());
  }

  download() {
    const fileName = this.submitted?.fileName;
    if (!fileName) return;

    const blob = new Blob([this.exportText()], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${fileName.replace(/\.[^/.]+$/, '')}.txt`;
    link.click();
    URL.revokeObjectURL(url);
  }
}

export const transcription = new TranscriptionState();

/* ---------------------------------------------------------------------------
 * Transport: `POST /api/transcription`
 * ------------------------------------------------------------------------- */

function toFormData(request: TranscriptionRequest): FormData {
  const form = new FormData();
  for (const [key, value] of Object.entries(request) as [string, unknown][]) {
    if (value === undefined) continue;
    if (value instanceof File) {
      form.append(key, value);
      continue;
    }

    const field = String(value).trim();
    if (field) form.append(key, field);
  }
  return form;
}

/**
 * Post the file and hand every segment to `onSegment` as it arrives.
 *
 * The body is a run of `data: {...}` lines, so each chunk is appended to
 * `pending` and only the complete lines are parsed - the tail is either empty
 * or half a frame, and it waits for the newline that ends it.
 */
async function streamTranscription(
  request: TranscriptionRequest,
  onSegment: (segment: TranscriptionSegment) => void
): Promise<void> {
  const response = await fetch('/api/transcription', {
    method: 'POST',
    body: toFormData(request)
  });
  if (!response.ok) {
    throw new Error((await response.text()) || `HTTP ${response.status}`);
  }
  if (!response.body) throw new Error('Response body is not readable');

  const emit = (line: string) => {
    const frame = line.startsWith('data:') ? line.slice(5).trim() : '';
    if (frame && frame !== '[DONE]') onSegment(JSON.parse(frame) as TranscriptionSegment);
  };

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let pending = '';

  while (true) {
    const { done, value } = await reader.read();
    if (value) pending += decoder.decode(value, { stream: true });
    const lines = pending.split('\n');
    pending = lines.pop() ?? '';
    for (const line of lines) emit(line);
    if (done) break;
  }
  // A stream that ends without a trailing newline still owes its last frame.
  emit(pending);
}
