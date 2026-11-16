<script lang="ts">
  import type { HTMLInputAttributes } from 'svelte/elements';

  type Props = HTMLInputAttributes;

  let { value = $bindable(), ...rest }: Props = $props();
</script>

<input bind:value {...rest} />

<style>
  input[type='text'],
  input[type='number'] {
    width: 100%;
    height: 28px;
    padding: 0 8px;
    font-family: var(--app-font);
    font-size: var(--app-fs-md);
    color: var(--app-text);
    background: var(--app-pane-alt);
    border: 1px solid var(--app-control-line);
    border-radius: var(--app-radius-sm);
    appearance: none;
  }
</style>
