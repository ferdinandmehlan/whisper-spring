<script lang="ts">
  import { onMount } from 'svelte';
  import { formatBytes } from '#/lib/components/format';
  import { models } from '#/lib/models/models.svelte';

  let origin = $state('');

  onMount(() => {
    origin = window.location.origin;
  });
</script>

<section id="server" class="app-group">
  <h3 class="app-eyebrow">server</h3>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Endpoint</span>
      <span class="app-row-desc"
        >This UI is served by the transcription server, so the origin is fixed.</span
      >
    </div>
    <div class="app-row-control">
      <span class="endpoint app-mono">{origin || '…'}</span>
    </div>
  </div>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Connection</span>
      <span class="app-row-desc">From the last request to the models endpoint.</span>
    </div>
    <div class="app-row-control">
      {#if models.status === 'error'}
        <span class="app-chip offline">offline</span>
      {:else if models.status === 'ready'}
        <span class="app-chip"><span class="app-dot"></span> reachable</span>
      {:else}
        <span class="app-chip">checking…</span>
      {/if}
    </div>
  </div>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Model</span>
      <span class="app-row-desc"
        >Loaded at startup, {formatBytes(models.loaded?.sizeBytes ?? null)}.</span
      >
    </div>
    <div class="app-row-control">
      <span class="endpoint app-mono">{models.loaded?.name ?? '—'}</span>
    </div>
  </div>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Endpoints</span>
      <span class="app-row-desc">What this UI talks to.</span>
    </div>
    <div class="app-row-control">
      <div class="endpoints app-mono">
        <span>POST /api/transcription</span>
        <span>WS /api/live</span>
        <span>GET /api/models</span>
      </div>
    </div>
  </div>
</section>

<style>
  .endpoint {
    overflow: hidden;
    font-size: var(--app-fs-xs);
    color: var(--app-text-2);
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .endpoints {
    display: flex;
    flex-direction: column;
    gap: 3px;
    font-size: var(--app-fs-xs);
    color: var(--app-text-2);
    text-align: right;
  }

  .offline {
    color: var(--app-danger);
    border-color: color-mix(in srgb, var(--app-danger) 40%, transparent);
  }

  @media (max-width: 760px) {
    .endpoints {
      text-align: left;
    }
  }
</style>
