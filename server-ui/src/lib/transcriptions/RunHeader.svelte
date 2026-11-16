<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import IconButton from '#/lib/components/IconButton.svelte';
  import { options } from '#/lib/transcriptions/options.svelte.js';
  import { transcription, type SubmittedRun } from '#/lib/transcriptions/transcriptions.svelte.js';

  let { submitted }: { submitted: SubmittedRun } = $props();
</script>

<div class="run">
  <div class="run-head">
    <div class="run-title">
      <span class="app-eyebrow">last run</span>
      <span class="file">{submitted.fileName}</span>
      <span class="app-chip">{submitted.fileSize}</span>
      {#if transcription.isLoading}
        <span class="app-chip">streaming</span>
      {/if}
    </div>

    <div class="run-tools">
      <IconButton
        aria-label="Include timestamps"
        aria-pressed={options.settings.includeTimestamps}
        title="Include timestamps in the view and the export"
        onclick={() => options.update({ includeTimestamps: !options.settings.includeTimestamps })}
      >
        <Icon name="clock" size={14} />
      </IconButton>
      <IconButton aria-label="Copy transcript" onclick={() => transcription.copy()}>
        <Icon name="copy" size={14} />
      </IconButton>
      <IconButton aria-label="Download transcript" onclick={() => transcription.download()}>
        <Icon name="download" size={14} />
      </IconButton>
    </div>
  </div>
  {#if submitted.prompt}
    <p class="run-prompt">
      <span class="app-eyebrow">prompt</span>
      {submitted.prompt}
    </p>
  {/if}
</div>

<style>
  .run {
    padding: 20px 2px 12px;
  }

  .run-head {
    display: flex;
    min-width: 0;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .run-title {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 8px;
  }

  .run-tools {
    display: flex;
    flex: none;
    gap: 2px;
  }

  .file {
    overflow: hidden;
    font-size: var(--app-fs-md);
    font-weight: 500;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .run-prompt {
    display: flex;
    align-items: baseline;
    gap: 8px;
    padding-top: 9px;
    font-size: var(--app-fs-md);
    color: var(--app-text-2);
  }
</style>
