<script lang="ts">
  import { formatBytes } from '#/lib/components/format';
  import { models } from '#/lib/models/models.svelte';

  let { modelCount }: { modelCount: number } = $props();
</script>

<section class="app-block" role="group" aria-labelledby="models-active">
  <h3 class="app-eyebrow" id="models-active">active model</h3>
  {#if models.status === 'error'}
    <div class="card-line">
      <span class="app-mono strong">unavailable</span>
      <span class="app-chip">offline</span>
    </div>
  {:else if models.loaded}
    <div class="card-line">
      <span class="app-mono strong">{models.loaded.name}</span>
      <span class="app-chip app-chip--solid">loaded</span>
    </div>
    <dl class="app-kv">
      <div class="app-kv-row">
        <dt>size</dt>
        <dd class="app-mono">{formatBytes(models.loaded.sizeBytes)}</dd>
      </div>
      <div class="app-kv-row">
        <dt>id</dt>
        <dd class="app-mono">{models.loaded.id}</dd>
      </div>
    </dl>
  {:else}
    <div class="card-line">
      <span class="app-mono strong dim">none loaded</span>
    </div>
  {/if}
</section>

<section class="app-block" role="group" aria-labelledby="models-storage">
  <h3 class="app-eyebrow" id="models-storage">storage</h3>
  <dl class="app-kv">
    <div class="app-kv-row">
      <dt>local models</dt>
      <dd class="app-mono">{modelCount}</dd>
    </div>
    <div class="app-kv-row">
      <dt>used</dt>
      <dd class="app-mono">{formatBytes(models.totalBytes)}</dd>
    </div>
  </dl>
</section>

<style>
  .card-line {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
  }

  .strong {
    font-size: var(--app-fs-sm);
  }

  .dim {
    color: var(--app-text-3);
  }
</style>
