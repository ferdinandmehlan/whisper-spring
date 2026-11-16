<script lang="ts">
  import { formatBytes } from '#/lib/components/format';
  import { models } from '#/lib/models/models.svelte';
</script>

<div class="storage">
  <div class="storage-head">
    <span class="app-eyebrow">model storage</span>
    <span class="app-mono app-value">
      {#if models.status === 'ready'}
        {formatBytes(models.totalBytes)}
      {:else if models.status === 'loading'}
        fetching…
      {:else}
        —
      {/if}
    </span>
  </div>
  <p class="app-hint hint">
    The catalogue is whatever <span class="app-mono">GET /api/models</span> reports. There is no local
    list and no fallback: if the server cannot answer, this view says so.
  </p>
</div>

<style>
  .storage {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 0 2px 22px;
  }

  .storage-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .hint {
    max-width: 76ch;
  }
</style>
