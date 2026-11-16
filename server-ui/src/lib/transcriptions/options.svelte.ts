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

/** Also what "restore defaults" writes back. */
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

const STORAGE_KEY = 'whisper-spring-settings';

function load(): TranscriptionSettings {
  if (typeof window === 'undefined') return { ...DEFAULT_SETTINGS };
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      return { ...DEFAULT_SETTINGS, ...(JSON.parse(stored) as Partial<TranscriptionSettings>) };
    }
  } catch {
    // Private mode, a full quota, or a value written by an older shape.
  }
  return { ...DEFAULT_SETTINGS };
}

function save(settings: TranscriptionSettings): void {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
  } catch {
    // Losing the preference is better than throwing out of an input handler.
  }
}

class OptionsState {
  settings = $state<TranscriptionSettings>({ ...DEFAULT_SETTINGS });

  #hydrated = false;

  hydrate() {
    if (this.#hydrated) return;
    this.settings = load();
    this.#hydrated = true;
  }

  persist() {
    save($state.snapshot(this.settings));
  }

  update(patch: Partial<TranscriptionSettings>) {
    Object.assign(this.settings, patch);
    this.persist();
  }

  reset() {
    this.settings = { ...DEFAULT_SETTINGS };
    this.persist();
  }
}

export const options = new OptionsState();
