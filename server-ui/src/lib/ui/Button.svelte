<script lang="ts">
  import type { Snippet } from 'svelte';
  import Icon from './Icon.svelte';

  type ButtonVariant = 'filled' | 'outlined' | 'text';
  type ButtonTone = 'default' | 'secondary' | 'error';

  type ButtonProps = {
    variant?: ButtonVariant;
    tone?: ButtonTone;
    icon?: string;
    children?: Snippet;
    disabled?: boolean;
    onclick?: (event: MouseEvent) => void;
    type?: 'button' | 'submit' | 'reset';
    flush?: boolean;
    'aria-label'?: string;
  };

  let {
    variant = 'filled',
    tone = 'default',
    icon,
    children,
    disabled = false,
    onclick,
    type = 'button',
    flush = false,
    'aria-label': ariaLabel
  }: ButtonProps = $props();

  // The visual definition lives in base.css (.btn primitives) so the same
  // look can be reused on an <a>; this component only selects the modifiers.
  const classes = $derived(
    [
      'btn',
      `btn--${variant}`,
      'ripple',
      !children && icon && 'btn--round',
      flush && 'btn--flush',
      tone === 'secondary' && 'btn--secondary',
      tone === 'error' && 'btn--error'
    ]
      .filter(Boolean)
      .join(' ')
  );
</script>

<button {type} {disabled} {onclick} aria-label={ariaLabel} class={classes}>
  {#if icon}
    <Icon {icon} />
  {/if}
  {#if children}
    {@render children()}
  {/if}
</button>
