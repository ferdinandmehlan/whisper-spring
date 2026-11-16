<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';

  let { side, label, onclick }: { side: 'left' | 'right'; label: string; onclick: () => void } =
    $props();
</script>

<button
  class="strip"
  data-side={side}
  aria-label={`Expand ${label}`}
  aria-keyshortcuts={side === 'left' ? '[' : ']'}
  title={`Expand ${label} (${side === 'left' ? '[' : ']'})`}
  {onclick}
>
  <Icon name={side === 'left' ? 'panelLeft' : 'panelRight'} size={13} />
  <span class="label">{label}</span>
  <span class="chevron"><Icon name="chevron" size={12} /></span>
</button>

<style>
  .strip {
    display: grid;
    height: 100%;
    place-items: center;
    grid-template-rows: auto 1fr auto;
    padding: 12px 0;
    color: var(--app-text-3);
    text-align: center;
    background: transparent;
    border: 0;
    border-radius: calc(var(--app-radius) - 1px);
    cursor: pointer;
    transition:
      color var(--app-dur) var(--app-ease),
      background-color var(--app-dur) var(--app-ease);
  }

  .strip:hover {
    color: var(--app-text);
    background: var(--app-hover);
  }

  .strip:focus-visible {
    outline-offset: -2px;
  }

  .label {
    place-self: center;
    font-family: var(--app-mono);
    font-size: var(--app-fs-2xs);
    font-weight: 500;
    letter-spacing: 0.14em;
    line-height: 1;
    text-transform: uppercase;
    writing-mode: vertical-rl;
  }

  .chevron {
    display: flex;
  }

  .strip[data-side='left'] .chevron {
    rotate: -90deg;
  }

  .strip[data-side='right'] .chevron {
    rotate: 90deg;
  }
</style>
