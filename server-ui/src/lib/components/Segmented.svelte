<script lang="ts" generics="T extends string">
  interface Props {
    label: string;
    segments: { value: T; label: string }[];
    active: string;
    onselect: (value: T) => void;
  }

  let { label, segments, active, onselect }: Props = $props();
</script>

<div
  class="segmented"
  role="group"
  aria-label={label}
  style:grid-template-columns={`repeat(${segments.length}, minmax(0, 1fr))`}
>
  {#each segments as segment (segment.value)}
    <button
      class="seg"
      aria-pressed={segment.value === active}
      onclick={() => onselect(segment.value)}
    >
      {segment.label}
    </button>
  {/each}
</div>

<style>
  .segmented {
    display: grid;
    gap: 2px;
    padding: 2px;
    border: 1px solid var(--app-control-line);
    border-radius: var(--app-radius-sm);
  }

  .seg {
    height: 24px;
    padding: 0 12px;
    font-size: var(--app-fs-sm);
    color: var(--app-text-2);
    background: transparent;
    border: 0;
    border-radius: 5px;
    cursor: pointer;
  }

  .seg:hover {
    color: var(--app-text);
    background: var(--app-hover);
  }

  .seg[aria-pressed='true'] {
    color: var(--app-on-fill);
    background: var(--app-fill);
  }
</style>
