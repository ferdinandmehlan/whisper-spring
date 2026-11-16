<script lang="ts">
  import Icon from '../Icon.svelte';
  import WorkArea from '../WorkArea.svelte';

  const languages = [
    'Auto-detect',
    'English',
    'German',
    'French',
    'Spanish',
    'Italian',
    'Portuguese',
    'Japanese',
    'Chinese'
  ];
</script>

<WorkArea crumb="settings" statusLeft="local · 18 keys" statusRight="stored in browser">
  {#snippet actions()}
    <button class="v2-btn v2-btn--ghost">Reset all</button>
    <span class="saved v2-mono"><Icon name="check" size={12} /> saved</span>
  {/snippet}

  <div class="cols">
    <section class="group">
      <h3 class="v2-eyebrow">transcription defaults</h3>

      <div class="row">
        <div class="row-label">
          <span class="title">Language</span>
          <span class="desc">Source language, or let the model detect it per file.</span>
        </div>
        <div class="row-control">
          <select aria-label="Language">
            {#each languages as l, i (l)}
              <option selected={i === 1}>{l}</option>
            {/each}
          </select>
        </div>
      </div>

      <div class="row">
        <div class="row-label">
          <span class="title">Translate to English</span>
          <span class="desc">Transcribe in the source language, emit English text.</span>
        </div>
        <div class="row-control">
          <button class="switch" aria-pressed="false" aria-label="Translate to English">
            <span></span>
          </button>
        </div>
      </div>

      <div class="row">
        <div class="row-label">
          <span class="title">Include timestamps</span>
          <span class="desc">Prefix every segment with its time range.</span>
        </div>
        <div class="row-control">
          <button class="switch" aria-pressed="true" aria-label="Include timestamps">
            <span></span>
          </button>
        </div>
      </div>
    </section>

    <section class="group">
      <h3 class="v2-eyebrow">decoding</h3>

      <div class="row">
        <div class="row-label">
          <span class="title">Temperature</span>
          <span class="desc">0 is deterministic, higher values explore.</span>
        </div>
        <div class="row-control slider">
          <input type="range" min="0" max="1" step="0.1" value="0.2" aria-label="Temperature" />
          <span class="value v2-mono">0.2</span>
        </div>
      </div>

      <div class="row">
        <div class="row-label">
          <span class="title">Beam size</span>
          <span class="desc">−1 uses greedy decoding.</span>
        </div>
        <div class="row-control">
          <select aria-label="Beam size">
            <option value="-1" selected>−1 · greedy</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
          </select>
        </div>
      </div>

      <div class="row">
        <div class="row-label">
          <span class="title">Entropy threshold</span>
          <span class="desc">Fallback when the decoder is uncertain.</span>
        </div>
        <div class="row-control slider">
          <input
            type="range"
            min="0"
            max="5"
            step="0.1"
            value="2.4"
            aria-label="Entropy threshold"
          />
          <span class="value v2-mono">2.4</span>
        </div>
      </div>
    </section>

    <section class="group">
      <h3 class="v2-eyebrow">server</h3>

      <div class="row">
        <div class="row-label">
          <span class="title">Endpoint</span>
          <span class="desc">Base URL of the transcription API.</span>
        </div>
        <div class="row-control">
          <input type="text" value="http://localhost:8080" aria-label="Endpoint" />
        </div>
      </div>

      <div class="row">
        <div class="row-label">
          <span class="title">Connection</span>
          <span class="desc">Last checked just now.</span>
        </div>
        <div class="row-control">
          <span class="v2-chip"><span class="v2-dot"></span> online · 3 ms</span>
        </div>
      </div>
    </section>
  </div>
</WorkArea>

<style>
  .cols {
    display: flex;
    flex-direction: column;
    gap: 26px;
  }

  .group {
    display: flex;
    flex-direction: column;
  }

  .group h3 {
    padding-bottom: 10px;
  }

  .row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    align-items: center;
    gap: 24px;
    padding: 12px 2px;
    border-top: 1px solid var(--v2-line);
  }

  .row-label {
    display: flex;
    min-width: 0;
    flex-direction: column;
    gap: 3px;
  }

  .title {
    font-size: 13px;
    letter-spacing: -0.01em;
  }

  .desc {
    font-size: 11.5px;
    line-height: 1.45;
    color: var(--v2-text-3);
  }

  .row-control {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
  }

  .row-control select {
    min-width: 150px;
  }

  .row-control input[type='text'] {
    width: 210px;
    font-family: var(--v2-mono);
    font-size: 11.5px;
  }

  .slider {
    min-width: 200px;
  }

  .value {
    width: 30px;
    font-size: 10.5px;
    color: var(--v2-text-2);
    text-align: right;
  }

  /* Switch: 30x17 hairline track, inverted knob when on. */
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
    transition:
      background-color var(--v2-dur) var(--v2-ease),
      border-color var(--v2-dur) var(--v2-ease);
  }

  .switch span {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 12px;
    height: 12px;
    background: var(--v2-text-3);
    border-radius: 50%;
    transition:
      transform var(--v2-dur) var(--v2-ease),
      background-color var(--v2-dur) var(--v2-ease);
  }

  .switch[aria-pressed='true'] {
    background: var(--v2-fill);
    border-color: var(--v2-fill);
  }

  .switch[aria-pressed='true'] span {
    background: var(--v2-on-fill);
    transform: translateX(14px);
  }

  .saved {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    height: 24px;
    padding: 0 8px;
    font-size: 10px;
    color: var(--v2-text-3);
  }

  @media (max-width: 720px) {
    .row {
      grid-template-columns: minmax(0, 1fr);
      gap: 8px;
    }

    .row-control {
      justify-content: flex-start;
    }

    .row-control input[type='text'] {
      width: 100%;
    }
  }
</style>
