import type { TranscriptionRequest, TranscriptionSegment, TranscriptionResponse } from './types';

export function formatTimestamp(ms: number): string {
  const totalSeconds = ms / 1000;
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = Math.floor(totalSeconds % 60);
  const milliseconds = Math.floor(ms % 1000);
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}.${String(milliseconds).padStart(3, '0')}`;
}

export function formatTimestampRange(start: number, end: number): string {
  return `[${formatTimestamp(start)} - ${formatTimestamp(end)}]`;
}

export async function transcribe(
  request: TranscriptionRequest,
  onSegment: (segment: TranscriptionSegment) => void
): Promise<TranscriptionResponse> {
  if (!request.file) {
    throw new Error('Please select an audio file');
  }

  const formData = new FormData();
  formData.append('stream', String(request.stream));
  formData.append('file', request.file);
  if (request.prompt) {
    formData.append('prompt', request.prompt.trim());
  }
  if (request.language) {
    formData.append('language', request.language);
  }
  if (request.translate !== undefined) {
    formData.append('translate', String(request.translate));
  }
  if (request.temperature !== undefined) {
    formData.append('temperature', String(request.temperature));
  }
  if (request.temperatureInc !== undefined) {
    formData.append('temperatureInc', String(request.temperatureInc));
  }
  if (request.offsetTMs !== undefined) {
    formData.append('offsetTMs', String(request.offsetTMs));
  }
  if (request.offsetN !== undefined) {
    formData.append('offsetN', String(request.offsetN));
  }
  if (request.durationMs !== undefined) {
    formData.append('durationMs', String(request.durationMs));
  }
  if (request.maxContext !== undefined) {
    formData.append('maxContext', String(request.maxContext));
  }
  if (request.maxLen !== undefined) {
    formData.append('maxLen', String(request.maxLen));
  }
  if (request.splitOnWord !== undefined) {
    formData.append('splitOnWord', String(request.splitOnWord));
  }
  if (request.bestOf !== undefined) {
    formData.append('bestOf', String(request.bestOf));
  }
  if (request.beamSize !== undefined) {
    formData.append('beamSize', String(request.beamSize));
  }
  if (request.audioContext !== undefined) {
    formData.append('audioContext', String(request.audioContext));
  }
  if (request.wordThreshold !== undefined) {
    formData.append('wordThreshold', String(request.wordThreshold));
  }
  if (request.entropyThreshold !== undefined) {
    formData.append('entropyThreshold', String(request.entropyThreshold));
  }
  if (request.logprobThreshold !== undefined) {
    formData.append('logprobThreshold', String(request.logprobThreshold));
  }

  const response = await fetch('/api/transcription', {
    method: 'POST',
    body: formData
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `HTTP ${response.status}`);
  }

  const reader = response.body?.getReader();
  if (!reader) {
    throw new Error('Response body is not readable');
  }

  const segments: TranscriptionSegment[] = [];
  const decoder = new TextDecoder();

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    const chunk = decoder.decode(value, { stream: true });
    for (const line of chunk.split('\n')) {
      if (line.startsWith('data:')) {
        const data = line.slice(5).trim();
        if (data && data !== '[DONE]') {
          try {
            const segment = JSON.parse(data) as TranscriptionSegment;
            segments.push(segment);
            onSegment(segment);
          } catch {
            // Ignore parse errors for incomplete chunks
          }
        }
      }
    }
  }

  return {
    segments,
    text: segments.map((s) => s.text.trim()).join(' ')
  };
}
