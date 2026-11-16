/**
 * Display formatting for the app panes.
 *
 * Kept free of Svelte imports so both the stores and the components can use it,
 * and so it is trivially unit-testable. There is one formatter per *shape*, not
 * per caller: the live clock, the transcript gutter and the export all read the
 * same `mm:ss`, and a second copy of that arithmetic is a bug waiting to happen.
 */

const BYTE_UNITS = ['kB', 'MB', 'GB', 'TB'];

/**
 * Bytes as a short human string: `77.7 MB`, `142 MB`, `3.1 GB`.
 *
 * One decimal below ten, none above: the models list is a table, and a column
 * of `77.7 MB / 142.0 MB / 3.1 GB` does not line up. A null size means the
 * server could not stat the file, which is a fact worth showing as `—` rather
 * than as a zero.
 */
export function formatBytes(bytes: number | null): string {
  if (bytes === null || !Number.isFinite(bytes)) return '—';
  if (bytes < 1024) return `${bytes} B`;

  let value = bytes / 1024;
  let unit = 0;
  while (value >= 1024 && unit < BYTE_UNITS.length - 1) {
    value /= 1024;
    unit += 1;
  }
  return `${value < 10 ? value.toFixed(1) : Math.round(value)} ${BYTE_UNITS[unit]}`;
}

/**
 * Milliseconds as `mm:ss`, for transcript gutters, elapsed clocks and status
 * strips. Minutes are not capped at 60: a 90-minute file reads `90:00`, which
 * is what a transcript gutter wants, not `01:30:00`.
 */
export function formatClock(ms: number): string {
  const totalSeconds = Math.floor(ms / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

/** Milliseconds as `HH:MM:SS.mmm`, for exported transcripts with timestamps. */
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
