<script lang="ts">
  import type { Snippet } from 'svelte';
  import type { HTMLSelectAttributes } from 'svelte/elements';

  interface Props extends HTMLSelectAttributes {
    children?: Snippet;
  }

  let { value = $bindable(), children, ...rest }: Props = $props();
</script>

<select bind:value {...rest}>
  {@render children?.()}
</select>

<style>
  select {
    width: 100%;
    height: 28px;
    padding: 0 26px 0 8px;
    font-family: var(--app-font);
    font-size: var(--app-fs-md);
    color: var(--app-text);
    background-color: var(--app-pane-alt);
    background-image: var(--app-chevron);
    background-repeat: no-repeat;
    background-position: right 8px center;
    border: 1px solid var(--app-control-line);
    border-radius: var(--app-radius-sm);
    appearance: none;
  }
</style>
