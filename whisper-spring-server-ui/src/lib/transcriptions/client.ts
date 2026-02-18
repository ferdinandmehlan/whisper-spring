import type { TranscriptionRequest, TranscriptionSegment, TranscriptionResponse } from './types';

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
