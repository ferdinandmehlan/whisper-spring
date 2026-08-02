<script lang="ts">
  import { twMerge } from 'tailwind-merge';

  type SelectProps = {
    id: string;
    label?: string;
    value?: string | number;
    options: { value: string | number; label: string }[];
    disabled?: boolean;
    class?: string;
    onchange?: () => void;
  };

  let {
    id,
    label,
    value = $bindable(),
    options,
    disabled = false,
    class: className = '',
    onchange
  }: SelectProps = $props();

  const baseClasses = 'rounded-lg border border-border bg-background px-3 py-2 text-sm';
  let mergedClasses = $derived(
    twMerge(baseClasses, disabled ? 'cursor-not-allowed opacity-50' : '', className)
  );
</script>

<div class="flex flex-col gap-1">
  {#if label}
    <label for={id} class="text-sm font-medium">{label}</label>
  {/if}
  <select
    {id}
    bind:value
    {disabled}
    onchange={() => {
      onchange?.();
    }}
    class={mergedClasses}
  >
    {#each options as opt (opt.value)}
      <option value={opt.value}>{opt.label}</option>
    {/each}
  </select>
</div>
