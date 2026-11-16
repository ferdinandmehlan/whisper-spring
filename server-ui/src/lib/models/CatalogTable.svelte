<script lang="ts">
  import { formatBytes } from '#/lib/components/format';
  import { models } from '#/lib/models/models.svelte';
</script>

<div class="table-head app-eyebrow">
  <span>model</span>
  <span class="col-size">size</span>
  <span class="col-state">status</span>
</div>

<ul class="list">
  {#each models.list as model (model.id)}
    <li class="row" class:active={model.id === models.active}>
      <div class="col-name">
        <span class="name app-mono">{model.name}</span>
        <span class="model-id">id · {model.id}</span>
      </div>
      <span class="col-size app-mono">{formatBytes(model.sizeBytes)}</span>
      <span class="col-state">
        {#if model.id === models.active}
          <span class="app-chip app-chip--solid">loaded</span>
        {:else}
          <span class="app-chip">available</span>
        {/if}
      </span>
    </li>
  {:else}
    <li class="empty">
      {#if models.status === 'loading' || models.status === 'idle'}
        <span class="app-mono">fetching…</span>
      {:else}
        <span>The server reports no loaded model. It starts without one unless</span>
        <span class="app-mono">whisper.model-path</span>
        <span>is set.</span>
      {/if}
    </li>
  {/each}
</ul>

<style>
  .table-head {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 80px 78px;
    gap: 12px;
    padding: 0 8px 8px;
    border-bottom: 1px solid var(--app-line);
  }

  .list {
    display: flex;
    flex-direction: column;
  }

  .row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 80px 78px;
    align-items: center;
    gap: 12px;
    padding: 10px 8px;
    border-bottom: 1px solid var(--app-line);
    transition: background-color var(--app-dur) var(--app-ease);
  }

  .row:hover {
    background: var(--app-hover);
  }

  .row.active {
    background: var(--app-pane-alt);
  }

  .col-name {
    display: flex;
    min-width: 0;
    flex-direction: column;
    gap: 2px;
  }

  .name {
    font-size: var(--app-fs-sm);
    letter-spacing: -0.01em;
  }

  .model-id {
    font-size: var(--app-fs-xs);
    color: var(--app-text-3);
  }

  .col-size {
    font-size: var(--app-fs-xs);
    color: var(--app-text-2);
  }

  .col-state {
    justify-self: start;
  }

  .empty {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    align-items: baseline;
    padding: 14px 8px;
    font-size: var(--app-fs-md);
    color: var(--app-text-3);
    border-bottom: 1px solid var(--app-line);
  }
</style>
