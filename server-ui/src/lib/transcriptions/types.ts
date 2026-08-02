export interface TranscriptionRequest {
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
  includeTimestamps?: boolean;
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

export interface TranscriptionSettings {
  includeTimestamps: boolean;
  language: string;
  translate: boolean;
  temperature: number;
  temperatureInc: number;
  offsetTMs: number;
  offsetN: number;
  durationMs: number;
  maxContext: number;
  maxLen: number;
  splitOnWord: boolean;
  bestOf: number;
  beamSize: number;
  audioContext: number;
  wordThreshold: number;
  entropyThreshold: number;
  logprobThreshold: number;
}

export const DEFAULT_SETTINGS: TranscriptionSettings = {
  includeTimestamps: false,
  language: 'auto',
  translate: false,
  temperature: 0.0,
  temperatureInc: 0.2,
  offsetTMs: 0,
  offsetN: 0,
  durationMs: 0,
  maxContext: -1,
  maxLen: 0,
  splitOnWord: false,
  bestOf: 2,
  beamSize: -1,
  audioContext: 0,
  wordThreshold: 0.01,
  entropyThreshold: 2.4,
  logprobThreshold: -1.0
};
