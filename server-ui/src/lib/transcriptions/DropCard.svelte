<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import Button from '#/lib/components/Button.svelte';
  import IconButton from '#/lib/components/IconButton.svelte';
  import TextArea from '#/lib/components/TextArea.svelte';
  import { transcription } from '#/lib/transcriptions/transcriptions.svelte';
  import { formatBytes } from '#/lib/components/format';

  let {
    browse,
    submit,
    clearFile
  }: { browse: () => void; submit: () => void; clearFile: () => void } = $props();
</script>

<section class="drop">
  {#if transcription.file}
    <div class="drop-form">
      <div class="file-chip">
        <Icon name="wave" size={14} />
        <span class="file-name">{transcription.file.name}</span>
        <span class="size app-mono">{formatBytes(transcription.file.size)}</span>
        <IconButton aria-label="Remove file" onclick={clearFile}>
          <Icon name="close" size={13} />
        </IconButton>
      </div>

      <TextArea
        class="prompt"
        rows={1}
        maxlength={500}
        placeholder="Add context for the transcription (optional)"
        aria-label="Transcription prompt"
        bind:value={transcription.prompt}
        onkeydown={(event) => {
          // Enter sends, Shift+Enter is a newline
          if (event.key === 'Enter' && !event.shiftKey && transcription.file) {
            event.preventDefault();
            submit();
          }
        }}
      ></TextArea>
    </div>

    <div class="drop-actions">
      <Button variant="ghost" onclick={browse}>
        <Icon name="upload" size={14} />
        Change file
      </Button>
      <Button variant="primary" disabled={transcription.isLoading} onclick={submit}>
        {transcription.isLoading ? 'Transcribing…' : 'Transcribe'}
      </Button>
    </div>
  {:else}
    <div class="drop-icon">
      <Icon name="wave" size={20} stroke={1.8} />
    </div>
    <h2 class="drop-title">Drop audio to transcribe</h2>
    <p class="drop-sub app-mono">wav · mp3 · m4a · flac · ogg</p>
    <div class="drop-actions">
      <Button variant="primary" onclick={browse}>
        <Icon name="folder" size={14} />
        Browse files
      </Button>
    </div>
  {/if}
</section>

<style>
  .drop {
    display: flex;
    flex: none;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 30px 24px;
    text-align: center;
    border: 1px dashed var(--app-line-strong);
    border-radius: var(--app-radius);
    transition: background-color var(--app-dur) var(--app-ease);
  }

  .drop-form {
    display: flex;
    flex-direction: column;
    gap: 6px;
    width: 100%;
    max-width: 560px;
    text-align: left;
  }

  .drop:hover {
    background: var(--app-hover);
  }

  .drop-icon {
    display: grid;
    width: 42px;
    height: 42px;
    place-items: center;
    border: 1px solid var(--app-line);
    border-radius: 10px;
  }

  .drop-title {
    font-size: var(--app-fs-xl);
    font-weight: 500;
    letter-spacing: -0.02em;
  }

  .drop-sub {
    font-size: var(--app-fs-xs);
    letter-spacing: 0.04em;
    color: var(--app-text-3);
  }

  .drop-actions {
    display: flex;
    gap: 8px;
    padding-top: 4px;
  }

  .file-chip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 2px 2px 8px;
    border-bottom: 1px solid var(--app-line);
  }

  .file-name {
    flex: 1;
    overflow: hidden;
    font-size: var(--app-fs-md);
    font-weight: 500;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .size {
    font-size: var(--app-fs-xs);
    color: var(--app-text-3);
  }

  .drop-form :global(.prompt) {
    width: 100%;
    padding: 4px 2px;
    resize: none;
    font-family: var(--app-font);
    font-size: var(--app-fs-md);
    color: var(--app-text);
    background: transparent;
    border: 0;
  }

  .drop-form :global(.prompt::placeholder) {
    color: var(--app-text-3);
  }

  .drop-form :global(.prompt:focus) {
    outline: none;
  }
</style>
