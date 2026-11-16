<script lang="ts">
  import { WAVE_BARS, live } from '#/lib/live/live.svelte.js';

  const bars = $derived(
    Array.from({ length: WAVE_BARS }, (_, index) => {
      const offset = WAVE_BARS - live.levels.length;
      const value = index < offset ? 0 : (live.levels[index - offset] ?? 0);
      return Math.round(Math.min(1, value * 2.5) * 100);
    })
  );
</script>

<div class="wave" aria-hidden="true">
  {#each bars as height, index (index)}
    <span class="bar" class:live={height > 2} style={`height:${Math.max(2, height)}%`}></span>
  {/each}
</div>

<style>
  .wave {
    display: flex;
    height: 96px;
    align-items: center;
    gap: 2px;
    padding: 0 2px;
    border-top: 1px solid var(--app-line);
    border-bottom: 1px solid var(--app-line);
  }

  .bar {
    flex: 1;
    min-width: 1px;
    background: var(--app-line-strong);
    border-radius: 1px;
  }

  .bar.live {
    background: var(--app-text);
  }
</style>
