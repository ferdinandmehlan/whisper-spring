<script lang="ts">
  import Icon from '../Icon.svelte';
  import WorkArea from '../WorkArea.svelte';

  const segments = [
    {
      t: '00:00',
      text: 'Good morning everyone, thanks for joining the weekly sync.',
      hl: 'everyone'
    },
    {
      t: '00:06',
      text: 'We shipped the streaming endpoint yesterday — latency is down to roughly four hundred milliseconds.'
    },
    { t: '00:14', text: 'The remaining work is the model download flow and the settings panel.' },
    { t: '00:21', text: 'Any blockers before we move on to the roadmap?' }
  ];
</script>

<WorkArea
  crumb="transcribe"
  statusLeft="tiny.en · english · streaming"
  statusRight="12:04 · 4 segments"
>
  {#snippet actions()}
    <button class="v2-btn v2-btn--ghost">
      <Icon name="upload" size={14} />
      Upload
    </button>
  {/snippet}

  <section class="drop">
    <div class="drop-icon">
      <Icon name="wave" size={20} stroke={1.8} />
    </div>
    <h2 class="drop-title">Drop audio to transcribe</h2>
    <p class="drop-sub v2-mono">wav · mp3 · m4a · flac · ogg · up to 500 MB</p>
    <div class="drop-actions">
      <button class="v2-btn v2-btn--primary">
        <Icon name="folder" size={14} />
        Browse files
      </button>
      <button class="v2-btn">
        <Icon name="mic" size={14} />
        Record
      </button>
    </div>
  </section>

  <div class="run-head">
    <div class="run-title">
      <span class="v2-eyebrow">last run</span>
      <span class="file">standup-2026-11-14.m4a</span>
      <span class="v2-chip">tiny.en</span>
      <span class="v2-chip">en</span>
    </div>
    <div class="run-actions">
      <button class="v2-icon-btn" aria-label="Copy transcript"
        ><Icon name="copy" size={14} /></button
      >
      <button class="v2-icon-btn" aria-label="Download transcript"
        ><Icon name="download" size={14} /></button
      >
    </div>
  </div>

  <div class="segments">
    {#each segments as seg, i (seg.t)}
      <article class="segment" class:lead={i === 0}>
        <span class="ts v2-mono">{seg.t}</span>
        <p class="text">
          {#if seg.hl}
            {@const at = seg.text.indexOf(seg.hl)}
            {seg.text.slice(0, at)}<span class="mark">{seg.hl}</span>{seg.text.slice(
              at + seg.hl.length
            )}
          {:else}
            {seg.text}
          {/if}
        </p>
      </article>
    {/each}
  </div>
</WorkArea>

<style>
  .drop {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 34px 24px;
    text-align: center;
    border: 1px dashed var(--v2-line-strong);
    border-radius: var(--v2-radius);
    transition: background-color var(--v2-dur) var(--v2-ease);
  }

  .drop:hover {
    background: var(--v2-hover);
  }

  .drop-icon {
    display: grid;
    width: 42px;
    height: 42px;
    place-items: center;
    border: 1px solid var(--v2-line);
    border-radius: 10px;
  }

  .drop-title {
    font-size: 15px;
    font-weight: 500;
    letter-spacing: -0.02em;
  }

  .drop-sub {
    margin-top: -6px;
    font-size: 10.5px;
    letter-spacing: 0.04em;
    color: var(--v2-text-3);
  }

  .drop-actions {
    display: flex;
    gap: 8px;
    padding-top: 4px;
  }

  .run-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 24px 2px 10px;
  }

  .run-title {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 8px;
  }

  .run-title .v2-eyebrow {
    margin-right: 2px;
  }

  .file {
    overflow: hidden;
    font-size: 12.5px;
    font-weight: 500;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .run-actions {
    display: flex;
    flex: none;
    gap: 2px;
  }

  .segments {
    display: flex;
    flex-direction: column;
    border-top: 1px solid var(--v2-line);
  }

  .segment {
    display: grid;
    grid-template-columns: 56px minmax(0, 1fr);
    gap: 14px;
    padding: 11px 8px;
    border-bottom: 1px solid var(--v2-line);
    transition: background-color var(--v2-dur) var(--v2-ease);
  }

  .segment:hover {
    background: var(--v2-hover);
  }

  .ts {
    font-size: 10.5px;
    color: var(--v2-text-3);
    padding-top: 3px;
  }

  .text {
    font-size: 13.5px;
    line-height: 1.65;
    letter-spacing: -0.008em;
    color: var(--v2-text-2);
  }

  .lead .text {
    color: var(--v2-text);
  }

  /* Word-level emphasis: inverted chip, no colour. */
  .mark {
    padding: 1px 4px;
    color: var(--v2-on-fill);
    background: var(--v2-fill);
    border-radius: 3px;
  }
</style>
