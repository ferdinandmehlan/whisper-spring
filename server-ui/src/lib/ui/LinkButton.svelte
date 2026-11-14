<script lang="ts">
  import { resolve } from '$app/paths';
  import type { RouteId } from '$app/types';
  import type { Snippet } from 'svelte';
  import Icon from './Icon.svelte';

  type LinkButtonProps = {
    href: RouteId;
    icon?: string;
    children?: Snippet;
  };

  let { href, icon, children }: LinkButtonProps = $props();
</script>

<a href={resolve(href)} class="link-button ripple">
  {#if icon}
    <Icon {icon} />
  {/if}
  {#if children}
    <span class="label rail-label">{@render children()}</span>
  {/if}
</a>

<style>
  .link-button {
    display: flex;
    align-items: center;
    gap: var(--space-3);
    padding: var(--space-3) var(--space-4);
    color: var(--color-secondary);
    border-radius: var(--radius-lg);
    transition:
      background-color var(--duration-short),
      color var(--duration-short);
  }

  .link-button:hover {
    color: var(--color-foreground);
    background-color: color-mix(in oklab, var(--color-secondary) 10%, transparent);
  }

  .label {
    font-weight: var(--weight-medium);
  }

  /* Matches the rail breakpoint in +layout.svelte. */
  @media (max-width: 60rem) {
    .link-button {
      justify-content: center;
    }
  }
</style>
