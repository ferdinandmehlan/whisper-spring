<script lang="ts">
  import Button from '#lib/ui/Button.svelte';
  import Icon from '#lib/ui/Icon.svelte';
  import OptionsSidebar from './OptionsSidebar.svelte';
  import {
    transcribe,
    formatTimestampRange,
    loadSettings,
    type TranscriptionRequest,
    type TranscriptionResponse
  } from '#lib/transcriptions';

  let isDragging = $state(false);
  let fileInput = $state<HTMLInputElement | null>(null);
  let input = $state<TranscriptionInput>({});
  let sentInput = $state<TranscriptionInput>();
  let isLoading = $state(false);
  let result = $state<TranscriptionResponse>({ segments: [], text: '' });
  let error = $state<string | null>(null);
  let showSettings = $state(false);

  function toggleSettings() {
    showSettings = !showSettings;
  }

  interface TranscriptionInput {
    file?: File;
    fileName?: string;
    fileSize?: string;
    prompt?: string;
  }

  // Dragging events
  function handleDragOver(event: DragEvent) {
    event.preventDefault();
  }

  function handleDragEnter() {
    isDragging = true;
  }

  function handleDragLeave(event: DragEvent) {
    const target = event.currentTarget as Node | null;
    const relatedTarget = event.relatedTarget as Node | null;
    if (target && !target.contains(relatedTarget)) {
      isDragging = false;
    }
  }

  function handleDrop(event: DragEvent) {
    event.preventDefault();
    isDragging = false;
    if (event.dataTransfer?.files?.[0]) {
      setFile(event.dataTransfer.files[0]);
    }
  }

  // File events
  function handleFileSelect(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      setFile(input.files[0]);
    }
  }

  function setFile(file: File) {
    input.file = file;
  }

  function clearFile() {
    input.file = undefined;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  // Submit
  function mapInputToRequest(input: TranscriptionInput): TranscriptionRequest {
    if (!input.file) {
      throw new Error('Please select an audio file');
    }

    return {
      file: input.file,
      stream: true,
      ...loadSettings()
    };
  }

  async function handleSubmit() {
    const request = mapInputToRequest(input);
    isLoading = true;
    sentInput = {
      fileName: input.file?.name,
      fileSize: ((input.file?.size ?? 0) / 1024 / 1024).toFixed(2) + ' MB',
      prompt: input.prompt
    };
    result = { segments: [], text: '' };
    error = null;
    reset();

    try {
      await transcribe(request, (segment) => {
        result.segments.push(segment);
        result.text += segment.text.trim() + ' ';
      });
    } catch (e) {
      error = e instanceof Error ? e.message : 'An error occurred';
    } finally {
      isLoading = false;
    }
  }

  function reset() {
    input = {};
    clearFile();
  }

  // Autoscroll to the bottom on a new segment
  $effect(() => {
    if (result.segments.length) {
      const scrollEl = document.getElementById('scroll');
      if (scrollEl) {
        scrollEl.scrollTop = scrollEl.scrollHeight;
      }
    }
  });

  function downloadAsTxt() {
    if (!sentInput?.fileName) return;
    const settings = loadSettings();
    const baseName = sentInput.fileName.replace(/\.[^/.]+$/, '');
    const content = settings.includeTimestamps
      ? result.segments
          .map((s) => `${formatTimestampRange(s.start, s.end)} ${s.text.trim()}`)
          .join('\n')
      : result.text;
    const blob = new Blob([content], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${baseName}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }
</script>

{#if isDragging}
  <div class="drop-overlay">
    <div class="drop-zone">
      <Icon icon="upload" size="lg" tone="primary" bounce />
      <span class="drop-label">Drop audio file</span>
    </div>
  </div>
{/if}

<div class="page transcriptions">
  <div
    ondragenter={handleDragEnter}
    ondragleave={handleDragLeave}
    ondragover={handleDragOver}
    ondrop={handleDrop}
    role="region"
    aria-label="Transcription workspace"
    class="stage"
  >
    {#if sentInput}
      <div class="request-row">
        <div class="bubble">
          <div class="bubble-line">
            <Icon icon="audio_file" tone="primary" />
            <span>{sentInput.fileName}</span>
            <span class="meta">({sentInput.fileSize})</span>
          </div>
          {#if sentInput.prompt}
            <div>
              <span class="muted">Prompt: </span>
              <span>{sentInput.prompt}</span>
            </div>
          {/if}
        </div>
      </div>
    {/if}

    {#if loadSettings().includeTimestamps && result.segments.length > 0}
      <div class="segments">
        {#each result.segments as segment (segment.start + segment.end)}
          <div>
            <span class="timestamp">{formatTimestampRange(segment.start, segment.end)}</span>
            {segment.text.trim()}
          </div>
        {/each}
      </div>
    {:else}
      <p class="transcript">{result.text}</p>
    {/if}

    {#if isLoading}
      <span class="loading"></span>
    {:else if error}
      <div class="error-box">
        {error}
      </div>
    {:else if result.text.length > 0}
      <div class="download">
        <Button
          onclick={downloadAsTxt}
          aria-label="Download transcription"
          icon="download"
          variant="text"
          flush
          tone="secondary"
        />
      </div>
    {:else}
      <div class="empty">
        <Icon icon="audio_file" size="lg" dim />
        <p class="empty-hint">Drop or upload an audio file to get started</p>
      </div>
    {/if}
  </div>

  <div class="composer-bar">
    <div class="composer">
      {#if input.file}
        <div class="file-chip">
          <Icon icon="audio_file" tone="primary" />
          <span class="file-name">{input.file.name}</span>
          <span class="meta">{(input.file.size / 1024 / 1024).toFixed(2)} MB</span>
          <Button
            tone="error"
            onclick={clearFile}
            aria-label="Remove file"
            icon="close"
            variant="text"
          />
        </div>
      {/if}

      <textarea
        id="prompt"
        bind:value={input.prompt}
        placeholder="Add context for the transcription (optional)"
        rows="1"
        class="prompt"
        maxlength="500"
        onkeydown={(e) => {
          if (e.key === 'Enter' && !e.shiftKey && input.file) {
            e.preventDefault();
            handleSubmit();
          }
        }}></textarea>

      <div class="composer-actions">
        <Button
          onclick={() => fileInput?.click()}
          aria-label="Upload audio"
          icon="upload"
          variant="text"
        />
        <div class="composer-actions-right">
          <Button onclick={toggleSettings} aria-label="Settings" icon="settings" variant="text" />
          <Button
            onclick={handleSubmit}
            disabled={!input.file}
            aria-label="Start transcription"
            icon="send"
            variant="outlined"
          >
            Transcribe
          </Button>
        </div>
      </div>
    </div>

    <!-- The upload Button above is the accessible control; this input must stay
         out of the tab order so keyboard users get one clear stop, not an
         invisible second one. It is visually hidden (not display:none) so that
         the file picker still works and the input remains programmatically
         focusable. -->
    <input
      type="file"
      accept="audio/*"
      class="visually-hidden"
      tabindex="-1"
      onchange={handleFileSelect}
      bind:this={fileInput}
    />
  </div>

  <OptionsSidebar {showSettings} onClose={() => (showSettings = false)} />
</div>

<style>
  /* Layout ---------------------------------------------------------------
     `.page` is the shared primitive; the composer bar supplies its own
     padding, so this page zeroes it out. */
  .transcriptions {
    align-items: center;
    padding: 0;
  }

  .stage {
    display: flex;
    /* min() instead of a fixed width so the column shrinks on narrow screens */
    width: min(var(--width-3xl), 100%);
    flex: 1 1 auto;
    flex-direction: column;
    padding: var(--space-4);
  }

  /* Drag and drop overlay */
  .drop-overlay {
    position: fixed;
    inset: 0;
    z-index: var(--z-drag);
    display: flex;
    align-items: center;
    justify-content: center;
    pointer-events: none;
    backdrop-filter: blur(var(--blur-sm));
  }

  .drop-zone {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: var(--space-4);
    padding: var(--space-8);
    border: 4px dashed var(--color-primary);
    border-radius: var(--radius-xl);
  }

  .drop-label {
    font-size: var(--text-xl);
    line-height: var(--leading-xl);
    font-weight: var(--weight-semibold);
  }

  /* Request bubble */
  .request-row {
    display: flex;
    justify-content: flex-end;
    margin-bottom: var(--space-3);
  }

  .bubble {
    display: flex;
    max-width: var(--width-md);
    flex-direction: column;
    gap: var(--space-2);
    padding: var(--space-4);
    background-color: var(--color-surface);
    border-radius: var(--radius-xl) var(--radius-xs) var(--radius-xl) var(--radius-xl);
  }

  .bubble-line {
    display: flex;
    align-items: center;
    gap: var(--space-2);
  }

  .meta {
    font-size: var(--text-sm);
    line-height: var(--leading-sm);
    color: var(--color-secondary);
  }

  .muted {
    color: var(--color-secondary);
  }

  /* Result */
  .segments {
    margin-left: var(--space-2);
    font-family: var(--font-mono);
    font-size: var(--text-sm);
    line-height: var(--leading-sm);
    white-space: pre-wrap;
  }

  .timestamp {
    color: var(--color-secondary);
  }

  .transcript {
    margin-left: var(--space-2);
    white-space: pre-wrap;
  }

  .loading {
    display: inline-block;
    width: var(--space-4);
    height: var(--space-4);
    margin: var(--space-2) 0 0 var(--space-2);
    background-color: var(--color-primary);
    border-radius: var(--radius-full);
    animation: pulse 1s cubic-bezier(0.4, 0, 0.6, 1) infinite;
  }

  .error-box {
    padding: var(--space-4);
    color: var(--color-foreground);
    background-color: color-mix(in oklab, var(--color-error) 10%, transparent);
    border: 1px solid var(--color-error);
    border-radius: var(--radius-md);
  }

  .download {
    display: flex;
    margin-top: var(--space-2);
  }

  .empty {
    display: flex;
    flex: 1 1 auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: var(--color-secondary);
    text-align: center;
  }

  .empty-hint {
    margin-top: var(--space-4);
  }

  /* Composer */
  .composer-bar {
    position: sticky;
    bottom: 0;
    z-index: var(--z-sticky);
    display: flex;
    width: 100%;
    justify-content: center;
    padding: var(--space-4);
    backdrop-filter: blur(var(--blur-lg));
  }

  .composer {
    width: min(var(--width-2xl), 100%);
    padding: var(--space-2);
    background-color: var(--color-surface);
    border-radius: var(--radius-xl);
  }

  .file-chip {
    display: flex;
    align-items: center;
    gap: var(--space-2);
    margin-bottom: var(--space-2);
    padding: var(--space-2) var(--space-3);
    background-color: var(--color-surface);
    border: 1px solid var(--color-border);
    border-radius: var(--radius-md);
  }

  .file-name {
    flex: 1;
    overflow: hidden;
    font-size: var(--text-sm);
    line-height: var(--leading-sm);
    font-weight: var(--weight-medium);
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .prompt {
    width: 100%;
    padding: var(--space-2) var(--space-3);
    resize: none;
    background-color: transparent;
    border: none;
    /* No `outline: none` here: the global :focus-visible ring in base.css is the
       only focus indicator this control has, and it is keyboard-only. */
    border-radius: var(--radius-md);
  }

  .composer-actions {
    display: flex;
    justify-content: space-between;
  }

  .composer-actions-right {
    display: flex;
    gap: var(--space-2);
  }
</style>
