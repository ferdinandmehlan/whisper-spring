<script lang="ts">
  import type { Snippet } from 'svelte';
  import type { HTMLButtonAttributes } from 'svelte/elements';

  interface Props extends HTMLButtonAttributes {
    children?: Snippet;
  }

  let { class: className, children, ...rest }: Props = $props();
</script>

<button class={className ? `app-icon-btn ${className}` : 'app-icon-btn'} {...rest}>
  {@render children?.()}
</button>

<style>
  .app-icon-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    flex: none;
    color: var(--app-text-2);
    background: transparent;
    border: 1px solid transparent;
    border-radius: var(--app-radius-sm);
    cursor: pointer;
    transition:
      background-color var(--app-dur) var(--app-ease),
      color var(--app-dur) var(--app-ease),
      border-color var(--app-dur) var(--app-ease);
  }

  .app-icon-btn:hover {
    color: var(--app-text);
    background: var(--app-hover);
    border-color: var(--app-line);
  }

  .app-icon-btn[aria-pressed='true'] {
    color: var(--app-on-fill);
    background: var(--app-fill);
  }
</style>
