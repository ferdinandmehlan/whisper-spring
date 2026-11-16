<script lang="ts">
  import { formatClock } from '#/lib/components/format';
  import { options } from '#/lib/transcriptions/options.svelte';
  import { transcription } from '#/lib/transcriptions/transcriptions.svelte';
</script>

{#if transcription.segments.length}
  <div class="segments" class:stamped={options.settings.includeTimestamps}>
    {#each transcription.segments as segment, index (index)}
      <article class="segment">
        {#if options.settings.includeTimestamps}
          <span class="ts app-mono">{formatClock(segment.start)}</span>
        {/if}
        <p class="text">{segment.text.trim()}</p>
      </article>
    {/each}
  </div>
{:else if transcription.isLoading}
  <p class="waiting app-mono">waiting for the first segment…</p>
{/if}

<style>
  .segments {
    display: flex;
    flex-direction: column;
    border-top: 1px solid var(--app-line);
  }

  .segment {
    display: grid;
    grid-template-columns: minmax(0, 1fr);
    gap: 14px;
    padding: 11px 2px;
    border-bottom: 1px solid var(--app-line);
    transition: background-color var(--app-dur) var(--app-ease);
  }

  .segment:hover {
    background: var(--app-hover);
  }

  .segments.stamped .segment {
    grid-template-columns: 56px minmax(0, 1fr);
  }

  .ts {
    padding-top: 3px;
    font-size: var(--app-fs-xs);
    color: var(--app-text-3);
  }

  .text {
    font-size: var(--app-fs-lg);
    line-height: 1.65;
    letter-spacing: -0.008em;
    color: var(--app-text-2);
  }

  .waiting {
    padding: 14px 2px;
    font-size: var(--app-fs-xs);
    letter-spacing: 0.04em;
    color: var(--app-text-3);
  }
</style>
