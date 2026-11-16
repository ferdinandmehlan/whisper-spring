<script lang="ts">
  import Icon from '../Icon.svelte';
  import WorkArea from '../WorkArea.svelte';

  // Deterministic peak heights: a mock must not reflow between renders.
  const bars = [
    14, 26, 44, 31, 62, 78, 52, 34, 22, 40, 58, 71, 88, 64, 42, 29, 18, 36, 55, 47, 33, 68, 82, 59,
    38, 24, 16, 30, 49, 66, 92, 74, 51, 35, 21, 28, 45, 61, 43, 27, 19, 34, 57, 76, 63, 41, 25, 17,
    23, 39, 54, 37, 22, 15, 31, 48, 68, 45, 29, 20, 13, 27, 42, 33, 19, 12, 21, 36
  ];
  const progress = 0.62;

  const lines = [
    { t: '00:00', text: 'Okay, so the idea is that we merge the three pages into a single shell.' },
    { t: '00:09', text: 'Left pane is navigation, centre is the work, right pane is context.' },
    {
      t: '00:17',
      text: 'That way switching between transcribe and live never loses the run you were doing.'
    }
  ];
</script>

<WorkArea crumb="live" statusLeft="live · 16 kHz" statusRight="00:42 · 3 segments">
  {#snippet actions()}
    <span class="live-chip v2-mono"><span class="pulse"></span> live</span>
    <button class="v2-icon-btn" aria-label="Copy transcript"><Icon name="copy" size={14} /></button>
  {/snippet}

  <div class="meta">
    <div class="elapsed v2-mono">00:42<span class="ms">.318</span></div>
    <div class="meta-right v2-mono">
      <span>macbook mic</span>
      <span class="sep">·</span>
      <span>16 kHz · mono</span>
      <span class="sep">·</span>
      <span>4.1× realtime</span>
    </div>
  </div>

  <div class="wave" aria-hidden="true">
    {#each bars as h, i (i)}
      <span class="bar" class:past={i / bars.length < progress} style={`height:${h}%`}></span>
    {/each}
  </div>

  <div class="stream">
    {#each lines as line (line.t)}
      <div class="line">
        <span class="ts v2-mono">{line.t}</span>
        <p>{line.text}</p>
      </div>
    {/each}
    <div class="line interim">
      <span class="ts v2-mono">00:26</span>
      <p>
        and the inspector on the right holds the options so the centre stays clean<span
          class="caret"
        ></span>
      </p>
    </div>
  </div>

  <div class="controls">
    <button class="rec" aria-label="Stop recording">
      <span class="rec-square"></span>
    </button>
    <div class="control-actions">
      <button class="v2-btn v2-btn--ghost">
        <Icon name="copy" size={14} />
        Copy
      </button>
      <button class="v2-btn v2-btn--ghost">Clear</button>
    </div>
  </div>
</WorkArea>

<style>
  .live-chip {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    height: 24px;
    padding: 0 8px;
    font-size: 10px;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: var(--v2-text);
    border: 1px solid var(--v2-line-strong);
    border-radius: 4px;
  }

  .pulse {
    width: 5px;
    height: 5px;
    background: var(--v2-danger);
    border-radius: 50%;
    animation: blink 1.6s ease-in-out infinite;
  }

  .meta {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 12px;
    padding: 4px 2px 14px;
  }

  .elapsed {
    font-size: 26px;
    font-weight: 300;
    letter-spacing: -0.03em;
    font-variant-numeric: tabular-nums;
  }

  .ms {
    color: var(--v2-text-3);
  }

  .meta-right {
    display: flex;
    gap: 7px;
    font-size: 10px;
    color: var(--v2-text-3);
  }

  .sep {
    opacity: 0.5;
  }

  .wave {
    display: flex;
    align-items: center;
    gap: 2px;
    height: 96px;
    padding: 0 2px;
    border-top: 1px solid var(--v2-line);
    border-bottom: 1px solid var(--v2-line);
  }

  .bar {
    flex: 1;
    min-width: 1px;
    background: var(--v2-line-strong);
    border-radius: 1px;
  }

  .bar.past {
    background: var(--v2-text);
  }

  .stream {
    padding-top: 14px;
  }

  .line {
    display: grid;
    grid-template-columns: 56px minmax(0, 1fr);
    gap: 14px;
    padding: 7px 8px;
    border-radius: var(--v2-radius-sm);
  }

  .line:hover {
    background: var(--v2-hover);
  }

  .ts {
    padding-top: 3px;
    font-size: 10.5px;
    color: var(--v2-text-3);
  }

  p {
    font-size: 13.5px;
    line-height: 1.65;
    letter-spacing: -0.008em;
  }

  .interim p {
    color: var(--v2-text-3);
  }

  .caret {
    display: inline-block;
    width: 6px;
    height: 15px;
    margin-left: 3px;
    vertical-align: -3px;
    background: var(--v2-text);
    animation: blink 1.1s step-end infinite;
  }

  .controls {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-top: 20px;
    padding-top: 16px;
    border-top: 1px solid var(--v2-line);
  }

  .rec {
    display: grid;
    width: 46px;
    height: 46px;
    flex: none;
    place-items: center;
    color: var(--v2-on-fill);
    background: var(--v2-fill);
    border: 0;
    border-radius: 50%;
    cursor: pointer;
    transition: opacity var(--v2-dur) var(--v2-ease);
  }

  .rec:hover {
    opacity: 0.85;
  }

  .rec-square {
    width: 12px;
    height: 12px;
    background: var(--v2-on-fill);
    border-radius: 2px;
  }

  .control-actions {
    display: flex;
    gap: 2px;
  }

  @keyframes blink {
    50% {
      opacity: 0.25;
    }
  }
</style>
