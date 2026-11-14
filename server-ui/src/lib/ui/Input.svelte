<script lang="ts">
  type InputProps = {
    id: string;
    label: string;
    value: string | number;
    type?: string;
    min?: number;
    formatValue?: (value: string | number) => string;
    onchange?: () => void;
  };

  let {
    id,
    label,
    value = $bindable(),
    type = 'text',
    min,
    formatValue,
    onchange
  }: InputProps = $props();
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
    {type}
    bind:value
    onchange={() => {
      onchange?.();
    }}
    class="control"
    {min}
  />
</div>
