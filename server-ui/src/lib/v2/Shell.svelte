<script lang="ts">
  import type { Snippet } from 'svelte';

  type Mode = 'dark' | 'light';

  let {
    left,
    main,
    right,
    mode = $bindable<Mode>('dark')
  }: {
    left: Snippet;
    main: Snippet;
    right: Snippet;
    mode?: Mode;
  } = $props();
</script>

<div class="v2" data-mode={mode}>
  <div class="v2-glass" aria-hidden="true"></div>

  <div class="v2-panes">
    <aside class="v2-pane v2-pane--left">{@render left()}</aside>
    <section class="v2-pane v2-pane--main">{@render main()}</section>
    <aside class="v2-pane v2-pane--right">{@render right()}</aside>
  </div>
</div>
