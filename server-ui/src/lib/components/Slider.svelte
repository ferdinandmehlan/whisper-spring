<script lang="ts">
  import type { HTMLInputAttributes } from 'svelte/elements';

  type Props = Omit<HTMLInputAttributes, 'type'>;

  let { value = $bindable(), ...rest }: Props = $props();
</script>

<input type="range" bind:value {...rest} />

<style>
  input[type='range'] {
    width: 100%;
    height: 18px;
    background: transparent;
    appearance: none;
    cursor: pointer;
  }

  input[type='range']::-webkit-slider-runnable-track {
    height: 2px;
    background: var(--app-control-line);
  }

  input[type='range']::-webkit-slider-thumb {
    width: 10px;
    height: 10px;
    margin-top: -4px;
    background: var(--app-fill);
    border: 0;
    border-radius: 50%;
    appearance: none;
  }

  input[type='range']::-moz-range-track {
    height: 2px;
    background: var(--app-control-line);
  }

  input[type='range']::-moz-range-thumb {
    width: 10px;
    height: 10px;
    background: var(--app-fill);
    border: 0;
    border-radius: 50%;
  }
</style>
