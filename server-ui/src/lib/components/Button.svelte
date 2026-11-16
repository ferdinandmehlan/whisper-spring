<script lang="ts">
  import type { Snippet } from 'svelte';
  import type { HTMLButtonAttributes } from 'svelte/elements';

  interface Props extends HTMLButtonAttributes {
    variant?: 'primary' | 'ghost';
    children?: Snippet;
  }

  let { variant, class: className, children, ...rest }: Props = $props();

  const classes = $derived(
    ['btn', variant && `btn--${variant}`, className].filter(Boolean).join(' ')
  );
</script>

<button class={classes} {...rest}>
  {@render children?.()}
</button>

<style>
  .btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 7px;
    height: 30px;
    padding: 0 12px;
    font-size: var(--app-fs-md);
    font-weight: 500;
    letter-spacing: -0.01em;
    color: var(--app-text);
    background: transparent;
    border: 1px solid var(--app-line-strong);
    border-radius: var(--app-radius-sm);
    cursor: pointer;
    white-space: nowrap;
    transition:
      background-color var(--app-dur) var(--app-ease),
      color var(--app-dur) var(--app-ease),
      border-color var(--app-dur) var(--app-ease);
  }

  .btn:hover {
    background: var(--app-hover);
  }

  .btn:disabled {
    cursor: not-allowed;
    opacity: 0.4;
  }

  .btn--primary {
    color: var(--app-on-fill);
    background: var(--app-fill);
    border-color: var(--app-fill);
  }

  .btn--primary:hover {
    background: var(--app-fill);
    opacity: 0.88;
  }

  .btn--ghost {
    border-color: transparent;
    color: var(--app-text-2);
  }

  .btn--ghost:hover {
    color: var(--app-text);
  }
</style>
