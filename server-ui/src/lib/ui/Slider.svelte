<script lang="ts">
  type SliderProps = {
    id: string;
    label: string;
    value: number;
    min?: number;
    max?: number;
    step?: number;
    formatValue?: (value: number) => string;
    oninput?: () => void;
  };

  let {
    id,
    label,
    value = $bindable(),
    min = 0,
    max = 100,
    step = 1,
    formatValue,
    oninput
  }: SliderProps = $props();
</script>

<div class="field">
  <label class="field-label" for={id}>
    {label}
    {#if formatValue}
      <span class="field-value">{formatValue(value)}</span>
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
    class="range"
  />
</div>

<style>
  /* A range input is not a `.control`: it has no box, padding or border. */
  .range {
    width: 100%;
    accent-color: var(--color-primary);
  }
</style>
