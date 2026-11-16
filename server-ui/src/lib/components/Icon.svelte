<script lang="ts">
  const paths: Record<string, string[]> = {
    transcribe: [
      'M14 3v4a1 1 0 0 0 1 1h4',
      'M5 21V5a2 2 0 0 1 2-2h7l5 5v13a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2z',
      'M9 13h6',
      'M9 17h4'
    ],
    mic: [
      'M12 3a3 3 0 0 0-3 3v6a3 3 0 0 0 6 0V6a3 3 0 0 0-3-3z',
      'M5 11a7 7 0 0 0 14 0',
      'M12 18v3',
      'M8 21h8'
    ],
    models: ['M12 3 3 8l9 5 9-5-9-5z', 'M3 13l9 5 9-5', 'M3 18l9 5 9-5'],
    settings: [
      'M4 7h5',
      'M15 7h5',
      'M4 12h9',
      'M19 12h1',
      'M4 17h3',
      'M13 17h7',
      'M11 5v4',
      'M17 10v4',
      'M9 15v4'
    ],
    upload: ['M12 16V4', 'M7 9l5-5 5 5', 'M4 20h16'],
    download: ['M12 4v12', 'M7 11l5 5 5-5', 'M4 20h16'],
    copy: ['M9 9h10v10H9z', 'M5 15V5h10'],
    clock: ['M12 3a9 9 0 1 0 0 18 9 9 0 0 0 0-18z', 'M12 7.5V12l3 2'],
    chevron: ['M6 9l6 6 6-6'],
    sun: [
      'M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z',
      'M12 2v2',
      'M12 20v2',
      'M2 12h2',
      'M20 12h2',
      'M5 5l1.5 1.5',
      'M17.5 17.5L19 19',
      'M19 5l-1.5 1.5',
      'M6.5 17.5L5 19'
    ],
    moon: ['M20 14.5A8.5 8.5 0 1 1 9.5 4a7 7 0 0 0 10.5 10.5z'],
    wave: ['M3 12v1', 'M6 9v6', 'M9 6v12', 'M12 10v4', 'M15 7v10', 'M18 11v2'],
    server: ['M4 5h16v6H4z', 'M4 13h16v6H4z', 'M7 8h.01', 'M7 16h.01', 'M11 8h6', 'M11 16h6'],
    close: ['M6 6l12 12', 'M18 6L6 18'],
    folder: ['M3 7h6l2 2h10v10H3z'],
    alert: ['M12 4l9 16H3L12 4z', 'M12 10v4', 'M12 17h.01'],
    panelLeft: ['M3 5h18v14H3z', 'M10 5v14'],
    panelRight: ['M3 5h18v14H3z', 'M14 5v14']
  };

  let {
    name,
    size = 16,
    stroke = 1.6,
    class: className = ''
  }: { name: string; size?: number; stroke?: number; class?: string } = $props();
</script>

<svg
  class={className}
  width={size}
  height={size}
  viewBox="0 0 24 24"
  fill="none"
  stroke="currentColor"
  stroke-width={stroke}
  stroke-linecap="round"
  stroke-linejoin="round"
  aria-hidden="true"
  focusable="false"
>
  {#each paths[name] ?? [] as d (d)}
    <path {d} />
  {/each}
</svg>
