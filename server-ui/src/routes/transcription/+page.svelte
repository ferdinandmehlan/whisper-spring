<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import WorkArea from '#/lib/layout/WorkArea.svelte';
  import { useInspector } from '#/lib/layout/inspector.svelte';
  import { formatClock } from '#/lib/components/format';
  import { transcription } from '#/lib/transcriptions/transcriptions.svelte';
  import DropCard from '#/lib/transcriptions/DropCard.svelte';
  import RunHeader from '#/lib/transcriptions/RunHeader.svelte';
  import SegmentList from '#/lib/transcriptions/SegmentList.svelte';
  import OptionsSection from '#/lib/transcriptions/OptionsSection.svelte';

  useInspector('options', inspector);

  let fileInput = $state<HTMLInputElement | null>(null);
  let dragDepth = $state(0);

  function browse() {
    fileInput?.click();
  }

  function pick(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    if (file) transcription.setFile(file);
    target.value = '';
  }

  function clearFile() {
    transcription.clearFile();
    if (fileInput) fileInput.value = '';
  }

  function onDragEnter(event: DragEvent) {
    if (!event.dataTransfer?.types.includes('Files')) return;
    dragDepth += 1;
    transcription.isDragging = true;
  }

  function onDragOver(event: DragEvent) {
    event.preventDefault();
  }

  function onDragLeave() {
    dragDepth = Math.max(0, dragDepth - 1);
    if (dragDepth === 0) transcription.isDragging = false;
  }

  function onDrop(event: DragEvent) {
    event.preventDefault();
    dragDepth = 0;
    transcription.isDragging = false;
    const file = event.dataTransfer?.files?.[0];
    if (file) transcription.setFile(file);
  }

  function submit() {
    void transcription.submit();
  }
</script>

{#snippet inspector()}
  <OptionsSection />
{/snippet}

<WorkArea
  crumb="transcription"
  statusLeft={transcription.isLoading
    ? 'streaming · /api/transcription'
    : 'idle · /api/transcription'}
  statusRight={transcription.segments.length
    ? `${transcription.segments.length} segments · ${formatClock(transcription.segments.at(-1)?.end ?? 0)}`
    : 'no run yet'}
>
  <div
    class="stage"
    role="region"
    aria-label="Transcription workspace"
    ondragenter={onDragEnter}
    ondragover={onDragOver}
    ondragleave={onDragLeave}
    ondrop={onDrop}
  >
    <DropCard {browse} {submit} {clearFile} />

    {#if transcription.submitted}
      <RunHeader submitted={transcription.submitted} />

      {#if transcription.error}
        <div class="error" role="alert">
          <Icon name="alert" size={15} />
          <div>
            <p class="error-text">{transcription.error}</p>
            <p class="error-hint">
              The server answered, but not with a transcript. Check that it is running and that a
              model is loaded.
            </p>
          </div>
        </div>
      {/if}

      <SegmentList />
    {/if}

    <input
      class="visually-hidden"
      type="file"
      accept="audio/*"
      tabindex="-1"
      onchange={pick}
      bind:this={fileInput}
    />
  </div>
</WorkArea>

{#if transcription.isDragging}
  <div class="drop-overlay" aria-hidden="true">
    <div class="drop-zone">
      <Icon name="upload" size={26} stroke={1.5} />
      <span class="drop-label">Drop audio file</span>
    </div>
  </div>
{/if}

<style>
  .stage {
    display: flex;
    min-height: 100%;
    flex-direction: column;
  }

  .error {
    display: flex;
    gap: 10px;
    margin-bottom: 18px;
    padding: 12px 14px;
    color: var(--app-text);
    background: color-mix(in srgb, var(--app-danger) 8%, transparent);
    border: 1px solid color-mix(in srgb, var(--app-danger) 45%, transparent);
    border-radius: var(--app-radius-sm);
  }

  .error :global(svg) {
    flex: none;
    margin-top: 2px;
    color: var(--app-danger);
  }

  .error-text {
    font-size: var(--app-fs-md);
    line-height: 1.5;
  }

  .error-hint {
    padding-top: 4px;
    font-size: var(--app-fs-sm);
    line-height: 1.5;
    color: var(--app-text-3);
  }

  .visually-hidden {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip-path: inset(50%);
    white-space: nowrap;
    border: 0;
  }

  .drop-overlay {
    position: fixed;
    inset: 0;
    z-index: 5;
    display: grid;
    place-items: center;
    pointer-events: none;
    background: color-mix(in srgb, var(--app-canvas) 55%, transparent);
    backdrop-filter: blur(6px);
  }

  .drop-zone {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 14px;
    padding: 44px 56px;
    color: var(--app-text);
    background: var(--app-pane);
    border: 1px dashed var(--app-line-strong);
    border-radius: var(--app-radius);
  }

  .drop-label {
    font-size: var(--app-fs-lg);
    font-weight: 500;
    letter-spacing: -0.01em;
  }
</style>
