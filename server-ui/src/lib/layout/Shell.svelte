<script lang="ts">
  import type { Snippet } from 'svelte';
  import { fade } from 'svelte/transition';
  import CollapsedStrip from './CollapsedStrip.svelte';

  let {
    left,
    main,
    right,
    leftCollapsed = $bindable(false),
    rightCollapsed = $bindable(false),
    leftLabel = 'workspace',
    rightLabel = () => 'inspector'
  }: {
    left: Snippet;
    main: Snippet;
    right: Snippet;
    leftCollapsed?: boolean;
    rightCollapsed?: boolean;
    leftLabel?: string;
    rightLabel?: () => string;
  } = $props();

  function motion(factor: number) {
    if (typeof window === 'undefined') return 0;
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 0 : factor;
  }

  const enter = () => ({ duration: motion(170), delay: motion(50) });
  const leave = () => ({ duration: motion(140) });
</script>

<div
  class="app"
  data-left={leftCollapsed ? 'collapsed' : 'open'}
  data-right={rightCollapsed ? 'collapsed' : 'open'}
>
  <div class="app-glass" aria-hidden="true"></div>

  <div class="app-panes">
    <aside class="app-pane app-pane--left" aria-label={leftLabel}>
      {#if leftCollapsed}
        <div class="pane-slot" in:fade={enter()}>
          <CollapsedStrip side="left" label={leftLabel} onclick={() => (leftCollapsed = false)} />
        </div>
      {:else}
        <div class="pane-slot" out:fade={leave()}>
          {@render left()}
        </div>
      {/if}
    </aside>

    <section class="app-pane app-pane--main" aria-label="work area">{@render main()}</section>

    <aside class="app-pane app-pane--right" aria-label={rightLabel()}>
      {#if rightCollapsed}
        <div class="pane-slot" in:fade={enter()}>
          <CollapsedStrip
            side="right"
            label={rightLabel()}
            onclick={() => (rightCollapsed = false)}
          />
        </div>
      {:else}
        <div class="pane-slot" out:fade={leave()}>
          {@render right()}
        </div>
      {/if}
    </aside>
  </div>
</div>
