export interface TranscriptionRequest {
  file: File;
  prompt?: string;
  stream: boolean;
}

export interface TranscriptionSegment {
  start: number;
  end: number;
  text: string;
}

export interface TranscriptionResponse {
  segments: TranscriptionSegment[];
  text: string;
}
