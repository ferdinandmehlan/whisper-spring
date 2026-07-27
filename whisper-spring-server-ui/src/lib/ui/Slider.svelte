<script lang="ts">
  import { twMerge } from 'tailwind-merge';

  type SliderProps = {
    id: string;
    label: string;
    value: number;
    min?: number;
    max?: number;
    step?: number;
    formatValue?: (value: number) => string;
    oninput?: () => void;
    class?: string;
  };

  let {
    id,
    label,
    value = $bindable(),
    min = 0,
    max = 100,
    step = 1,
    formatValue,
    oninput,
    class: className = ''
  }: SliderProps = $props();
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
    type="range"
    {min}
    {max}
    {step}
    bind:value
    oninput={() => {
      oninput?.();
    }}
    class={twMerge('w-full', className)}
  />
</div>
