<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import { live } from '#/lib/live/live.svelte.js';
</script>

<div class="transport">
  <button
    class="rec"
    class:recording={live.isRecording}
    aria-label={live.isRecording ? 'Stop recording' : 'Start recording'}
    title={live.isRecording ? 'Stop recording' : 'Start recording'}
    disabled={live.isConnecting}
    onclick={() => live.toggle()}
  >
    {#if live.isRecording}
      <span class="square"></span>
    {:else}
      <Icon name="mic" size={19} />
    {/if}
  </button>

  {#if live.phase === 'idle' && !live.messages.length}
    <p class="title">Nothing captured yet</p>
    <p class="hint">Press record and speak. Audio is streamed as 16 kHz mono PCM to the socket.</p>
  {/if}
</div>

<style>
  .transport {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 18px 0 4px;
  }

  .rec {
    display: grid;
    width: 52px;
    height: 52px;
    flex: none;
    place-items: center;
    color: var(--app-on-fill);
    background: var(--app-fill);
    border: 0;
    border-radius: 50%;
    cursor: pointer;
    transition: opacity var(--app-dur) var(--app-ease);
  }

  .rec:hover:not(:disabled) {
    opacity: 0.85;
  }

  .rec:disabled {
    cursor: progress;
    opacity: 0.45;
  }

  .rec.recording {
    box-shadow:
      0 0 0 3px var(--app-pane),
      0 0 0 4px var(--app-line-strong);
  }

  .square {
    width: 13px;
    height: 13px;
    background: var(--app-on-fill);
    border-radius: 2px;
  }

  .title {
    margin-top: 8px;
    font-size: var(--app-fs-lg);
    color: var(--app-text-2);
  }

  .hint {
    max-width: 42ch;
    font-size: var(--app-fs-sm);
    line-height: 1.55;
    text-align: center;
    color: var(--app-text-3);
  }
</style>
