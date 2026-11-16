<script lang="ts">
  import { onMount } from 'svelte';
  import Icon from './Icon.svelte';

  type View = 'transcribe' | 'live' | 'models' | 'settings';
  type Mode = 'dark' | 'light';

  let {
    view = $bindable<View>('transcribe'),
    mode = $bindable<Mode>('dark')
  }: { view?: View; mode?: Mode } = $props();

  // app.html already resolved the stored/system mode onto <html> before first
  // paint; this only syncs the Svelte state with it so the toggle reflects
  // reality (and the shell's data-mode matches the resolved palette).
  onMount(() => {
    const initial = document.documentElement.dataset.v2Mode;
    if (initial === 'light' || initial === 'dark') mode = initial;
  });

  function setMode(next: Mode) {
    mode = next;
    document.documentElement.dataset.v2Mode = next;
    try {
      localStorage.setItem('v2-mode', next);
    } catch {
      // Persisting is best effort; the attribute above already applied it.
    }
  }

  const nav: { id: View; label: string; icon: string; key: string }[] = [
    { id: 'transcribe', label: 'Transcribe', icon: 'transcribe', key: '1' },
    { id: 'live', label: 'Live', icon: 'mic', key: '2' },
    { id: 'models', label: 'Models', icon: 'models', key: '3' }
  ];

  const recent = [
    { name: 'standup-2026-11-14.m4a', meta: '12:04', state: 'done' },
    { name: 'interview-raw.wav', meta: '48:31', state: 'done' },
    { name: 'podcast-ep-12.mp3', meta: '1:02:11', state: 'run' },
    { name: 'lecture-07.flac', meta: '32:09', state: 'fail' }
  ];
</script>

<div class="rail">
  <header class="brand">
    <div class="mark">
      <Icon name="wave" size={15} stroke={2} />
    </div>
    <div class="brand-text">
      <span class="name">whisper<span class="thin"> spring</span></span>
      <span class="v2-eyebrow">v2 · islands</span>
    </div>
  </header>

  <nav class="nav" aria-label="Workspace">
    {#each nav as item (item.id)}
      <button
        class="item"
        class:active={view === item.id}
        aria-current={view === item.id ? 'page' : undefined}
        onclick={() => (view = item.id)}
      >
        <Icon name={item.icon} size={15} />
        <span class="label">{item.label}</span>
        <kbd class="key">{item.key}</kbd>
      </button>
    {/each}
  </nav>

  <div class="v2-hr"></div>

  <section class="recent">
    <div class="recent-head">
      <span class="v2-eyebrow">recent</span>
      <button class="v2-icon-btn" aria-label="Search transcripts">
        <Icon name="search" size={13} />
      </button>
    </div>

    <ul class="recent-list v2-scroll">
      {#each recent as item (item.name)}
        <li>
          <button class="recent-item">
            <span class="state" class:run={item.state === 'run'} class:fail={item.state === 'fail'}
            ></span>
            <span class="recent-name">{item.name}</span>
            <span class="recent-meta v2-mono">{item.meta}</span>
          </button>
        </li>
      {/each}
    </ul>
  </section>

  <footer class="rail-foot">
    <button class="item" class:active={view === 'settings'} onclick={() => (view = 'settings')}>
      <Icon name="settings" size={15} />
      <span class="label">Settings</span>
      <kbd class="key">,</kbd>
    </button>

    <div class="status">
      <span class="v2-dot"></span>
      <span class="endpoint v2-mono">localhost:8080</span>

      <div class="modes" role="group" aria-label="Theme">
        <button
          class="mode"
          aria-pressed={mode === 'dark'}
          aria-label="Dark theme"
          title="Dark theme"
          onclick={() => setMode('dark')}
        >
          <Icon name="moon" size={12} />
        </button>
        <button
          class="mode"
          aria-pressed={mode === 'light'}
          aria-label="Light theme"
          title="Light theme"
          onclick={() => setMode('light')}
        >
          <Icon name="sun" size={12} />
        </button>
      </div>
    </div>
  </footer>
</div>

<style>
  .rail {
    display: flex;
    height: 100%;
    flex-direction: column;
    padding: 0 8px;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 9px;
    height: 52px;
    padding: 0 8px;
  }

  .mark {
    display: grid;
    width: 26px;
    height: 26px;
    flex: none;
    place-items: center;
    color: var(--v2-on-fill);
    background: var(--v2-fill);
    border-radius: 7px;
  }

  .brand-text {
    display: flex;
    min-width: 0;
    flex-direction: column;
    gap: 3px;
  }

  .name {
    font-size: 13px;
    font-weight: 600;
    letter-spacing: -0.02em;
    line-height: 1;
    white-space: nowrap;
  }

  .thin {
    color: var(--v2-text-3);
    font-weight: 400;
  }

  .nav {
    display: flex;
    flex-direction: column;
    gap: 1px;
    padding: 6px 0 10px;
  }

  .item {
    display: flex;
    align-items: center;
    gap: 9px;
    width: 100%;
    height: 32px;
    padding: 0 8px;
    font-size: 12.5px;
    color: var(--v2-text-2);
    text-align: left;
    background: transparent;
    border: 0;
    border-radius: var(--v2-radius-sm);
    cursor: pointer;
    transition:
      background-color var(--v2-dur) var(--v2-ease),
      color var(--v2-dur) var(--v2-ease);
  }

  .item:hover {
    color: var(--v2-text);
    background: var(--v2-hover);
  }

  .item.active {
    color: var(--v2-text);
    background: var(--v2-active);
  }

  .item.active :global(svg) {
    color: var(--v2-text);
  }

  .label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .key {
    font-family: var(--v2-mono);
    font-size: 10px;
    color: var(--v2-text-3);
    opacity: 0;
    transition: opacity var(--v2-dur) var(--v2-ease);
  }

  .item:hover .key,
  .item:focus-visible .key {
    opacity: 1;
  }

  .recent {
    display: flex;
    min-height: 0;
    flex: 1;
    flex-direction: column;
    padding-top: 12px;
  }

  .recent-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 8px 6px;
  }

  .recent-list {
    display: flex;
    flex-direction: column;
    gap: 1px;
    margin: 0;
    padding: 0;
    list-style: none;
  }

  .recent-item {
    display: grid;
    grid-template-columns: 7px minmax(0, 1fr) auto;
    align-items: center;
    gap: 7px;
    width: 100%;
    padding: 7px 8px;
    font-size: 12px;
    color: var(--v2-text-2);
    text-align: left;
    background: transparent;
    border: 0;
    border-radius: var(--v2-radius-sm);
    cursor: pointer;
  }

  .recent-item:hover {
    color: var(--v2-text);
    background: var(--v2-hover);
  }

  .state {
    width: 5px;
    height: 5px;
    background: var(--v2-text-3);
    border-radius: 50%;
  }

  .state.run {
    background: var(--v2-text);
    animation: blink 1.4s ease-in-out infinite;
  }

  .state.fail {
    background: var(--v2-danger);
  }

  .recent-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .recent-meta {
    font-size: 10px;
    color: var(--v2-text-3);
  }

  .rail-foot {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 8px 0 4px;
    border-top: 1px solid var(--v2-line);
  }

  .status {
    display: flex;
    align-items: center;
    gap: 7px;
    padding: 0 6px 0 8px;
    color: var(--v2-text-3);
  }

  .status .v2-dot {
    color: var(--v2-text);
  }

  .endpoint {
    flex: 1;
    overflow: hidden;
    font-size: 10px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  /* Segmented mode switch. Two labelled buttons rather than one toggle: the
     icon alone never told anyone a light theme existed. */
  .modes {
    display: flex;
    flex: none;
    gap: 1px;
    padding: 1px;
    border: 1px solid var(--v2-line);
    border-radius: 6px;
  }

  .mode {
    display: grid;
    width: 20px;
    height: 18px;
    place-items: center;
    padding: 0;
    color: var(--v2-text-3);
    background: transparent;
    border: 0;
    border-radius: 4px;
    cursor: pointer;
    transition:
      color var(--v2-dur) var(--v2-ease),
      background-color var(--v2-dur) var(--v2-ease);
  }

  .mode:hover {
    color: var(--v2-text);
    background: var(--v2-hover);
  }

  .mode[aria-pressed='true'] {
    color: var(--v2-on-fill);
    background: var(--v2-fill);
  }

  @keyframes blink {
    50% {
      opacity: 0.35;
    }
  }

  /* Below 1024px the pane becomes a 64px rail: labels leave the visual tree,
     the icon and its accessible name stay. */
  @media (max-width: 1024px) {
    .brand-text,
    .label,
    .key,
    .recent,
    .endpoint {
      display: none;
    }

    .rail {
      align-items: center;
      padding: 0 4px;
    }

    .brand {
      justify-content: center;
      padding: 0;
    }

    .item {
      justify-content: center;
      width: 32px;
      padding: 0;
    }

    .rail-foot {
      width: 100%;
      align-items: center;
    }
  }
</style>
