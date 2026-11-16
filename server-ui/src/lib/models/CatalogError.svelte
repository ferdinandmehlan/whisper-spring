<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import { models } from '#/lib/models/models.svelte.js';
</script>

<div class="error" role="alert">
  <Icon name="alert" size={15} />
  <div>
    <p class="error-text">GET /api/models failed: {models.error}</p>
    <p class="error-hint">The server may be down, or the endpoint may be missing.</p>
  </div>
</div>

<style>
  .error {
    display: flex;
    gap: 10px;
    margin-bottom: 18px;
    padding: 12px 14px;
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
</style>
