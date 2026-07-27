import { DEFAULT_SETTINGS, type TranscriptionSettings } from './types';

const STORAGE_KEY = 'whisper-spring-settings';

export function loadSettings(): TranscriptionSettings {
  if (typeof window === 'undefined') return { ...DEFAULT_SETTINGS };
  try {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      return { ...DEFAULT_SETTINGS, ...JSON.parse(stored) };
    }
  } catch {
    // Ignore
  }
  return { ...DEFAULT_SETTINGS };
}

export function saveSettings(settings: TranscriptionSettings): void {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(settings));
  } catch {
    // Ignore
  }
}

export function resetSettings(): TranscriptionSettings {
  const defaults = { ...DEFAULT_SETTINGS };
  saveSettings(defaults);
  return defaults;
}

export function hasModifiedSettings(settings: TranscriptionSettings): boolean {
  return Object.keys(DEFAULT_SETTINGS).some(
    (key) =>
      settings[key as keyof TranscriptionSettings] !==
      DEFAULT_SETTINGS[key as keyof TranscriptionSettings]
  );
}
