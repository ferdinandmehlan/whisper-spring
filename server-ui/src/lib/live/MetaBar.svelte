<script lang="ts">
  import { live } from '#/lib/live/live.svelte';
  import { formatClock } from '#/lib/components/format';
</script>

<div class="meta">
  <div class="elapsed app-mono">{formatClock(live.elapsedMs)}</div>
</div>

<style>
  .meta {
    padding: 4px 2px 14px;
  }

  .elapsed {
    font-size: var(--app-fs-display);
    font-weight: 300;
    letter-spacing: -0.03em;
    font-variant-numeric: tabular-nums;
  }
</style>
