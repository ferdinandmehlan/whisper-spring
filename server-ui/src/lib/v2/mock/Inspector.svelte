<script lang="ts">
  import Icon from '../Icon.svelte';

  type View = 'transcribe' | 'live' | 'models' | 'settings';
  let { view }: { view: View } = $props();

  const titles: Record<View, string> = {
    transcribe: 'options',
    live: 'input',
    models: 'details',
    settings: 'sections'
  };

  const presets = ['fast', 'balanced', 'accurate'];

  const advanced = [
    ['beam size', '−1 · greedy'],
    ['best of', '2'],
    ['temperature inc', '0.2'],
    ['entropy thr.', '2.4'],
    ['logprob thr.', '−1.0'],
    ['max context', 'unlimited']
  ];

  const sectionLinks = ['transcription defaults', 'decoding', 'server', 'keyboard', 'about'];

  // Input level meter: 18 fixed segments, the first 12 lit.
  const levelTicks = Array.from({ length: 18 }, (_, i) => i);
  const levelLit = 12;
</script>

<header class="head">
  <span class="v2-eyebrow">{titles[view]}</span>
  <button class="v2-icon-btn" aria-label="Reset options">
    <Icon name="settings" size={13} />
  </button>
</header>

<div class="body v2-scroll">
  {#if view === 'transcribe'}
    <section class="block">
      <span class="label v2-eyebrow">preset</span>
      <div class="segmented">
        {#each presets as p, i (p)}
          <button class="seg" aria-pressed={i === 1}>{p}</button>
        {/each}
      </div>
    </section>

    <section class="block">
      <div class="field">
        <span class="label v2-eyebrow">model</span>
        <select aria-label="Model">
          <option selected>ggml-tiny.en</option>
          <option>ggml-base.en</option>
          <option>ggml-small.en</option>
        </select>
      </div>

      <div class="field">
        <span class="label v2-eyebrow">language</span>
        <select aria-label="Language">
          <option selected>English</option>
          <option>Auto-detect</option>
          <option>German</option>
        </select>
      </div>

      <div class="field">
        <div class="field-head">
          <span class="label v2-eyebrow">temperature</span>
          <span class="v2-mono value">0.2</span>
        </div>
        <input type="range" min="0" max="1" step="0.1" value="0.2" aria-label="Temperature" />
      </div>
    </section>

    <section class="block">
      <div class="block-head">
        <span class="label v2-eyebrow">advanced</span>
        <button class="v2-icon-btn" aria-label="Show advanced options"
          ><Icon name="chevron" size={13} /></button
        >
      </div>
      <dl class="kv">
        {#each advanced as [k, v] (k)}
          <div class="kv-row">
            <dt>{k}</dt>
            <dd class="v2-mono">{v}</dd>
          </div>
        {/each}
      </dl>
    </section>
  {:else if view === 'live'}
    <section class="block">
      <span class="label v2-eyebrow">device</span>
      <select aria-label="Input device">
        <option selected>MacBook Pro Microphone</option>
        <option>AirPods Pro</option>
        <option>Scarlett Solo</option>
      </select>
    </section>

    <section class="block">
      <div class="field-head">
        <span class="label v2-eyebrow">input level</span>
        <span class="v2-mono value">−12 dB</span>
      </div>
      <div class="level">
        {#each levelTicks as tick (tick)}
          <span class="tick" class:on={tick < levelLit}></span>
        {/each}
      </div>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">streaming</span>
      <div class="toggle-row">
        <span>Partial results</span>
        <button class="switch" aria-pressed="true" aria-label="Partial results"
          ><span></span></button
        >
      </div>
      <div class="toggle-row">
        <span>Voice activity detection</span>
        <button class="switch" aria-pressed="false" aria-label="Voice activity detection"
          ><span></span></button
        >
      </div>
      <div class="toggle-row">
        <span>Auto-punctuate</span>
        <button class="switch" aria-pressed="true" aria-label="Auto-punctuate"><span></span></button
        >
      </div>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">session</span>
      <dl class="kv">
        <div class="kv-row">
          <dt>latency</dt>
          <dd class="v2-mono">412 ms</dd>
        </div>
        <div class="kv-row">
          <dt>buffer</dt>
          <dd class="v2-mono">4096</dd>
        </div>
        <div class="kv-row">
          <dt>model</dt>
          <dd class="v2-mono">tiny.en</dd>
        </div>
      </dl>
    </section>
  {:else if view === 'models'}
    <section class="block">
      <span class="label v2-eyebrow">active model</span>
      <div class="card-line">
        <span class="v2-mono strong">ggml-tiny.en</span>
        <span class="v2-chip v2-chip--solid">loaded</span>
      </div>
      <p class="hint">Loaded into memory by the server. Switching reloads the model (~1.2 s).</p>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">storage</span>
      <dl class="kv">
        <div class="kv-row">
          <dt>local models</dt>
          <dd class="v2-mono">2</dd>
        </div>
        <div class="kv-row">
          <dt>used</dt>
          <dd class="v2-mono">2.4 GB</dd>
        </div>
        <div class="kv-row">
          <dt>path</dt>
          <dd class="v2-mono">/models</dd>
        </div>
      </dl>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">hints</span>
      <p class="hint">
        Prefer <span class="v2-mono">tiny.en</span> for live sessions and
        <span class="v2-mono">small.en</span> for files. Large models widen the right pane's latency estimate.
      </p>
    </section>
  {:else}
    <section class="block">
      <span class="label v2-eyebrow">jump to</span>
      <nav class="links">
        {#each sectionLinks as s (s)}
          <a href="#{s.replaceAll(' ', '-')}">{s}</a>
        {/each}
      </nav>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">about</span>
      <dl class="kv">
        <div class="kv-row">
          <dt>ui</dt>
          <dd class="v2-mono">v2 · islands</dd>
        </div>
        <div class="kv-row">
          <dt>server</dt>
          <dd class="v2-mono">0.1.0</dd>
        </div>
        <div class="kv-row">
          <dt>whisper.cpp</dt>
          <dd class="v2-mono">1.7.4</dd>
        </div>
      </dl>
    </section>

    <section class="block">
      <span class="label v2-eyebrow">reset</span>
      <button class="v2-btn">Restore defaults</button>
      <p class="hint">Clears stored options in this browser. Model files stay on the server.</p>
    </section>
  {/if}
</div>

<style>
  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 52px;
    flex: none;
    padding: 0 var(--v2-pad);
    border-bottom: 1px solid var(--v2-line);
  }

  .body {
    display: flex;
    min-height: 0;
    flex: 1;
    flex-direction: column;
    gap: 20px;
    padding: var(--v2-pad);
  }

  .block {
    display: flex;
    flex-direction: column;
    gap: 9px;
  }

  .block-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .label {
    display: block;
  }

  .value {
    font-size: 10.5px;
    color: var(--v2-text-2);
  }

  .field {
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .field-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .field select {
    width: 100%;
  }

  .segmented {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 2px;
    padding: 2px;
    border: 1px solid var(--v2-line);
    border-radius: var(--v2-radius-sm);
  }

  .seg {
    height: 24px;
    font-size: 11.5px;
    color: var(--v2-text-2);
    background: transparent;
    border: 0;
    border-radius: 5px;
    cursor: pointer;
  }

  .seg:hover {
    color: var(--v2-text);
    background: var(--v2-hover);
  }

  .seg[aria-pressed='true'] {
    color: var(--v2-on-fill);
    background: var(--v2-fill);
  }

  .kv {
    display: flex;
    flex-direction: column;
  }

  .kv-row {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 12px;
    padding: 6px 0;
    border-top: 1px solid var(--v2-line);
    font-size: 11.5px;
  }

  .kv-row dt {
    color: var(--v2-text-3);
  }

  .kv-row dd {
    font-size: 10.5px;
    color: var(--v2-text-2);
  }

  .level {
    display: flex;
    gap: 2px;
    height: 10px;
  }

  .tick {
    flex: 1;
    background: var(--v2-line);
    border-radius: 1px;
  }

  .tick.on {
    background: var(--v2-fill);
  }

  .toggle-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 7px 0;
    font-size: 12px;
    color: var(--v2-text-2);
    border-top: 1px solid var(--v2-line);
  }

  .card-line {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
    border-top: 1px solid var(--v2-line);
  }

  .strong {
    font-size: 11.5px;
  }

  .hint {
    font-size: 11.5px;
    line-height: 1.55;
    color: var(--v2-text-3);
  }

  .links {
    display: flex;
    flex-direction: column;
  }

  .links a {
    padding: 7px 0;
    font-size: 12px;
    color: var(--v2-text-2);
    text-decoration: none;
    border-top: 1px solid var(--v2-line);
  }

  .links a:hover {
    color: var(--v2-text);
  }

  .switch {
    position: relative;
    width: 32px;
    height: 18px;
    flex: none;
    padding: 0;
    background: transparent;
    border: 1px solid var(--v2-line-strong);
    border-radius: 99px;
    cursor: pointer;
  }

  .switch span {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 12px;
    height: 12px;
    background: var(--v2-text-3);
    border-radius: 50%;
    transition: transform var(--v2-dur) var(--v2-ease);
  }

  .switch[aria-pressed='true'] {
    background: var(--v2-fill);
    border-color: var(--v2-fill);
  }

  .switch[aria-pressed='true'] span {
    background: var(--v2-on-fill);
    transform: translateX(14px);
  }
</style>
