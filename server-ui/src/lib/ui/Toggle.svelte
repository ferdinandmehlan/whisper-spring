<script lang="ts">
  type ToggleProps = {
    checked?: boolean;
    label?: string;
    disabled?: boolean;
    onchange?: (checked: boolean) => void;
  };

  let { checked = $bindable(false), label, disabled = false, onchange }: ToggleProps = $props();

  function handleChange(event: Event) {
    const target = event.target as HTMLInputElement;
    checked = target.checked;
    onchange?.(checked);
  }
</script>

<label class="toggle" class:disabled>
  <input type="checkbox" bind:checked {disabled} onchange={handleChange} class="visually-hidden" />
  <span class="track"></span>
  {#if label}
    <span class="field-label">{label}</span>
  {/if}
</label>

<style>
  .toggle {
    /* Positioned so the visually hidden input stays inside the label */
    position: relative;
    display: inline-flex;
    align-items: center;
    gap: var(--space-3);
    cursor: pointer;
  }

  .toggle.disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  .track {
    --switch-inset: calc((var(--size-switch-height) - var(--size-switch-knob)) / 2);

    position: relative;
    width: var(--size-switch-width);
    height: var(--size-switch-height);
    border-radius: var(--radius-full);
    background-color: color-mix(in oklab, var(--color-secondary) 30%, transparent);
    transition: background-color var(--duration-short);
  }

  .track::after {
    content: '';
    position: absolute;
    top: var(--switch-inset);
    inset-inline-start: var(--switch-inset);
    width: var(--size-switch-knob);
    height: var(--size-switch-knob);
    border-radius: var(--radius-full);
    background-color: var(--color-background);
    transition: translate var(--duration-short);
  }

  .toggle input:checked + .track {
    background-color: var(--color-primary);
  }

  /* Travel equals the knob width because the insets are symmetric */
  .toggle input:checked + .track::after {
    translate: 100% 0;
  }

  /* The input itself is visually hidden, so the track has to show focus */
  .toggle input:focus-visible + .track {
    outline: 2px solid var(--color-primary-text);
    outline-offset: 2px;
  }
</style>
