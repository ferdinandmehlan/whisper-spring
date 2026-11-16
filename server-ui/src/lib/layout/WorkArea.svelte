<script lang="ts">
  import type { Snippet } from 'svelte';

  let {
    crumb,
    statusLeft,
    statusRight,
    children
  }: {
    crumb: string;
    statusLeft?: string;
    statusRight?: string;
    children: Snippet;
  } = $props();
</script>

<header class="bar">
  <div class="crumbs">
    <span class="dim app-mono">workspace</span>
    <span class="slash app-mono">/</span>
    <span class="current">{crumb}</span>
  </div>
</header>

<div class="body app-scroll">
  {@render children()}
</div>

{#if statusLeft || statusRight}
  <footer class="status app-mono">
    <span>{statusLeft}</span>
    <span class="fill"></span>
    {#if statusRight}<span>{statusRight}</span>{/if}
  </footer>
{/if}

<style>
  .bar {
    display: flex;
    height: 52px;
    flex: none;
    align-items: center;
    gap: 12px;
    padding: 0 var(--app-pad);
    border-bottom: 1px solid var(--app-line);
  }

  .crumbs {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 7px;
    font-size: var(--app-fs-md);
  }

  .dim {
    color: var(--app-text-3);
  }

  .slash {
    color: var(--app-text-3);
  }

  .current {
    overflow: hidden;
    font-weight: 500;
    letter-spacing: -0.01em;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .body {
    min-height: 0;
    flex: 1;
    padding: var(--app-pad);
  }

  .status {
    display: flex;
    height: 26px;
    flex: none;
    align-items: center;
    gap: 8px;
    padding: 0 var(--app-pad);
    font-size: var(--app-fs-2xs);
    letter-spacing: 0.02em;
    color: var(--app-text-3);
    border-top: 1px solid var(--app-line);
  }

  .fill {
    flex: 1;
  }
</style>
