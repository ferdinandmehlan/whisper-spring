<script lang="ts">
  import Button from '$lib/ui/Button.svelte';
  import Icon from '$lib/ui/Icon.svelte';
  import {
    transcribe,
    type TranscriptionRequest,
    type TranscriptionResponse
  } from '$lib/transcriptions';

  let isDragging = $state(false);
  let fileInput = $state<HTMLInputElement | null>(null);

  let input = $state<TranscriptionInput>({});
  let sentInput = $state<TranscriptionInput>();
  let isLoading = $state(false);
  let result = $state<TranscriptionResponse>({ segments: [], text: '' });
  let error = $state<string | null>(null);

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

  function setFile(file: File | undefined) {
    input.file = file;
  }

  function clearFile() {
    setFile(undefined);
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
      prompt: input.prompt
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
    const baseName = sentInput.fileName.replace(/\.[^/.]+$/, '');
    const blob = new Blob([result.text], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${baseName}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }
</script>

{#if isDragging}
  <div
    class="pointer-events-none fixed inset-0 z-9999 flex items-center justify-center backdrop-blur-sm"
  >
    <div
      class="flex flex-col items-center gap-4 rounded-2xl border-4 border-dashed border-primary p-8"
    >
      <Icon icon="upload" class="animate-bounce text-4xl! text-primary" />
      <span class="text-xl font-semibold">Drop audio file</span>
    </div>
  </div>
{/if}

<div class="flex min-h-0 flex-1 flex-col items-center">
  <div
    ondragenter={handleDragEnter}
    ondragleave={handleDragLeave}
    ondragover={handleDragOver}
    ondrop={handleDrop}
    role="application"
    class="flex w-3xl flex-auto flex-col p-4"
  >
    {#if sentInput}
      <div class="mb-3 flex justify-end">
        <div
          class="flex max-w-md flex-col gap-2 rounded-tl-2xl rounded-tr-xs rounded-br-2xl rounded-bl-2xl bg-surface p-4"
        >
          <div class="flex items-center gap-2">
            <Icon icon="audio_file" class="text-primary" />
            <span>{sentInput.fileName}</span>
            <span class="text-sm text-secondary">({sentInput.fileSize})</span>
          </div>
          {#if sentInput.prompt}
            <div>
              <span class="text-secondary">Prompt: </span>
              <span>{sentInput.prompt}</span>
            </div>
          {/if}
        </div>
      </div>
    {/if}

    <p class="ml-2 whitespace-pre-wrap">{result.text}</p>

    {#if isLoading}
      <span
        class="mt-2 ml-2 inline-block h-4 w-4 animate-pulse rounded-full bg-primary [animation-duration:1s]"
      >
      </span>
    {:else if error}
      <div class="rounded-lg border border-error bg-error/10 p-4 text-error">
        {error}
      </div>
    {:else if result.text.length > 0}
      <div class="mt-2 flex">
        <Button
          onclick={downloadAsTxt}
          aria-label="Download transcription"
          icon="download"
          variant="text"
          class="p-0 text-secondary"
        />
      </div>
    {:else}
      <div class="flex-auto content-center text-center text-secondary">
        <Icon icon="audio_file" class="mx-auto text-4xl! opacity-50" />
        <p class="mt-4">Drop or upload an audio file to get started</p>
      </div>
    {/if}
  </div>

  <div class="sticky bottom-0 z-30 flex w-full justify-center p-4 backdrop-blur-lg">
    <div class="w-2xl rounded-2xl bg-surface p-2">
      {#if input.file}
        <div
          class="mb-2 flex items-center gap-2 rounded-lg border border-border bg-surface px-3 py-2"
        >
          <Icon icon="audio_file" class="text-primary" />
          <span class="flex-1 truncate text-sm font-medium">{input.file.name}</span>
          <span class="text-sm text-secondary">
            {(input.file.size / 1024 / 1024).toFixed(2)} MB
          </span>
          <Button
            class="text-error"
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
        class="without-ring field-sizing-content w-full resize-none border-none bg-transparent"
        maxlength="500"
        onkeydown={(e) => {
          if (e.key === 'Enter' && !e.shiftKey && input.file) {
            e.preventDefault();
            handleSubmit();
          }
        }}
      ></textarea>

      <div class="flex justify-between">
        <Button
          onclick={() => fileInput?.click()}
          aria-label="Upload audio"
          icon="upload"
          variant="text"
        />
        <Button
          onclick={handleSubmit}
          disabled={!input.file}
          aria-label="Start transcription"
          icon="send"
          variant="outlined"
          class="text-forground border-forground"
        >
          Transcribe
        </Button>
      </div>
    </div>

    <input
      type="file"
      accept="audio/*"
      class="hidden"
      onchange={handleFileSelect}
      bind:this={fileInput}
    />
  </div>
</div>
