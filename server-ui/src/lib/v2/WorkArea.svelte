<script lang="ts">
  import type { Snippet } from 'svelte';
  import Icon from './Icon.svelte';

  let {
    crumb,
    actions,
    children,
    statusLeft = 'api · localhost:8080',
    statusRight = '16 kHz · mono'
  }: {
    crumb: string;
    actions?: Snippet;
    children: Snippet;
    statusLeft?: string;
    statusRight?: string;
  } = $props();
</script>

<header class="bar">
  <div class="crumbs">
    <span class="dim v2-mono">workspace</span>
    <span class="slash v2-mono">/</span>
    <span class="current">{crumb}</span>
  </div>

  <div class="bar-actions">
    {#if actions}
      {@render actions()}
    {/if}
    <button class="v2-icon-btn" aria-label="More actions">
      <Icon name="more" size={14} />
    </button>
  </div>
</header>

<div class="body v2-scroll">
  {@render children()}
</div>

<footer class="status v2-mono">
  <span>{statusLeft}</span>
  <span class="fill"></span>
  <span>{statusRight}</span>
  <span class="sep">·</span>
  <span>ready</span>
</footer>

<style>
  .bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    height: 52px;
    flex: none;
    padding: 0 var(--v2-pad);
    border-bottom: 1px solid var(--v2-line);
  }

  .crumbs {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 7px;
    font-size: 12.5px;
  }

  .dim {
    color: var(--v2-text-3);
  }

  .slash {
    color: var(--v2-text-3);
  }

  .current {
    overflow: hidden;
    font-weight: 500;
    letter-spacing: -0.01em;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .bar-actions {
    display: flex;
    flex: none;
    align-items: center;
    gap: 4px;
  }

  .body {
    min-height: 0;
    flex: 1;
    padding: var(--v2-pad);
  }

  .status {
    display: flex;
    align-items: center;
    gap: 8px;
    height: 26px;
    flex: none;
    padding: 0 var(--v2-pad);
    font-size: 10px;
    letter-spacing: 0.02em;
    color: var(--v2-text-3);
    border-top: 1px solid var(--v2-line);
  }

  .fill {
    flex: 1;
  }

  .sep {
    opacity: 0.5;
  }
</style>
