<script lang="ts">
  import { twMerge } from 'tailwind-merge';

  type InputProps = {
    id: string;
    label: string;
    value: string | number;
    type?: string;
    min?: number;
    formatValue?: (value: string | number) => string;
    onchange?: () => void;
    class?: string;
  };

  let {
    id,
    label,
    value = $bindable(),
    type = 'text',
    min,
    formatValue,
    onchange,
    class: className = ''
  }: InputProps = $props();

  const baseClasses = 'rounded-lg border border-border bg-background px-3 py-2 text-sm';
  let mergedClasses = $derived(twMerge(baseClasses, className));
</script>

<div class="flex flex-col gap-1">
  <label for={id} class="text-sm font-medium">
    {label}
    {#if formatValue}
      <span class="text-secondary">{formatValue(value)}</span>
    {/if}
  </label>
  <input
    {id}
    {type}
    bind:value
    onchange={() => {
      onchange?.();
    }}
    class={mergedClasses}
    {min}
  />
</div>
