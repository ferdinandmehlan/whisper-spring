<script lang="ts">
  type SelectProps = {
    id: string;
    label?: string;
    value?: string | number;
    options: { value: string | number; label: string }[];
    disabled?: boolean;
    onchange?: () => void;
  };

  let {
    id,
    label,
    value = $bindable(),
    options,
    disabled = false,
    onchange
  }: SelectProps = $props();
</script>

<div class="field">
  {#if label}
    <label class="field-label" for={id}>{label}</label>
  {/if}
  <select
    {id}
    bind:value
    {disabled}
    onchange={() => {
      onchange?.();
    }}
    class="control select"
  >
    {#each options as opt (opt.value)}
      <option value={opt.value}>{opt.label}</option>
    {/each}
  </select>
</div>

<style>
  /* A native <select> cannot host a pseudo-element chevron and data URIs
     cannot use currentColor, so the icon comes from --select-chevron. */
  .select {
    padding-right: var(--space-8);
    background-image: var(--select-chevron);
    background-repeat: no-repeat;
    background-position: right var(--space-2) center;
    background-size: 1.5em 1.5em;
  }
</style>
