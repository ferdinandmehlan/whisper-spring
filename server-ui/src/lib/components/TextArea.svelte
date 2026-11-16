<script lang="ts">
  import type { HTMLTextareaAttributes } from 'svelte/elements';

  interface Props extends Omit<HTMLTextareaAttributes, 'value'> {
    value?: string;
  }

  let { class: className, value = $bindable(), ...rest }: Props = $props();
</script>

<textarea class={className} bind:value {...rest}></textarea>
