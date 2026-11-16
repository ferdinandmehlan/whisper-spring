<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import IconButton from '#/lib/components/IconButton.svelte';
  import type { InspectorContent } from '#/lib/layout/inspector.svelte';
  let { inspector, oncollapse }: { inspector?: InspectorContent; oncollapse?: () => void } =
    $props();
  const title = $derived(inspector?.title ?? 'Inspector');
</script>

<header class="head">
  <span class="app-eyebrow">{title}</span>
  <div class="head-actions">
    {#if oncollapse}
      <IconButton
        aria-label={`Collapse ${title}`}
        aria-keyshortcuts="]"
        title={`Collapse ${title} (])`}
        onclick={oncollapse}
      >
        <Icon name="panelRight" size={13} />
      </IconButton>
    {/if}
  </div>
</header>

<div class="body app-scroll">
  {@render inspector?.content()}
</div>

<style>
  .head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 52px;
    flex: none;
    padding: 0 var(--app-pad);
    border-bottom: 1px solid var(--app-line);
  }

  .head-actions {
    display: flex;
    flex: none;
    align-items: center;
    gap: 2px;
  }

  .body {
    display: flex;
    min-height: 0;
    flex: 1;
    flex-direction: column;
    gap: 20px;
    padding: var(--app-pad);
  }
</style>
