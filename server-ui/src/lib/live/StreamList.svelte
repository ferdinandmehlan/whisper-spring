<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import IconButton from '#/lib/components/IconButton.svelte';
  import Button from '#/lib/components/Button.svelte';
  import { live } from '#/lib/live/live.svelte.js';
</script>

<div class="stream">
  {#if live.messages.length}
    <div class="head">
      <span class="app-sub">transcript</span>
      <div class="actions">
        <IconButton
          aria-label="Copy transcript"
          title="Copy transcript"
          onclick={() => navigator.clipboard.writeText(live.transcript.trim())}
        >
          <Icon name="copy" size={14} />
        </IconButton>
        <Button variant="ghost" class="clear" onclick={() => live.clear()}>Clear</Button>
      </div>
    </div>
  {/if}

  {#each live.messages as message, index (index)}
    <p class="line">{message}</p>
  {/each}

  {#if live.isRecording}
    <span class="caret"></span>
  {/if}
</div>

<style>
  .stream {
    padding-top: 16px;
  }

  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 0 8px 6px;
  }

  .actions {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .actions :global(.clear) {
    height: 26px;
    padding: 0 8px;
    font-size: var(--app-fs-sm);
    font-weight: 400;
    color: var(--app-text-3);
    border-color: transparent;
  }

  .actions :global(.clear:hover) {
    color: var(--app-text);
    border-color: var(--app-line);
  }

  .line {
    padding: 7px 8px;
    font-size: var(--app-fs-lg);
    line-height: 1.65;
    letter-spacing: -0.008em;
    color: var(--app-text-2);
    border-radius: var(--app-radius-sm);
  }

  .line:hover {
    background: var(--app-hover);
  }

  .line:last-of-type {
    color: var(--app-text);
  }

  .caret {
    display: inline-block;
    width: 6px;
    height: 15px;
    margin: 0 0 2px 3px;
    vertical-align: -3px;
    background: var(--app-text);
    animation: blink 1.1s step-end infinite;
  }

  @keyframes blink {
    50% {
      opacity: 0.25;
    }
  }
</style>
