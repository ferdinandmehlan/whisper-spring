<script lang="ts">
  import { BLOCK_SIZE, SAMPLE_RATE, live } from '#/lib/live/live.svelte';

  const TICKS = 18;
  const trimmed = $derived(Math.min(1, live.level * 2.5));
  const lit = $derived(Math.round(trimmed * TICKS));
  const percent = $derived(Math.round(trimmed * 100));
</script>

<section class="app-block" role="group" aria-labelledby="live-general">
  <h3 class="app-eyebrow" id="live-general">general</h3>
  <div class="app-field-head">
    <span class="app-sub">level</span>
    <span class="app-mono app-value">{percent}%</span>
  </div>
  <div class="level" aria-hidden="true">
    {#each Array.from({ length: TICKS }, (_, index) => index) as tick (tick)}
      <span class="tick" class:on={tick < lit}></span>
    {/each}
  </div>
</section>

<section class="app-block" role="group" aria-labelledby="live-session">
  <h3 class="app-eyebrow" id="live-session">session</h3>
  <dl class="app-kv">
    <div class="app-kv-row">
      <dt>state</dt>
      <dd class="app-mono">{live.phase}</dd>
    </div>
    <div class="app-kv-row">
      <dt>segments</dt>
      <dd class="app-mono">{live.segments}</dd>
    </div>
  </dl>
</section>

<section class="app-block" role="group" aria-labelledby="live-stream">
  <h3 class="app-eyebrow" id="live-stream">stream</h3>
  <dl class="app-kv">
    <div class="app-kv-row">
      <dt>sample rate</dt>
      <dd class="app-mono">{SAMPLE_RATE / 1000} kHz</dd>
    </div>
    <div class="app-kv-row">
      <dt>channels</dt>
      <dd class="app-mono">mono</dd>
    </div>
    <div class="app-kv-row">
      <dt>encoding</dt>
      <dd class="app-mono">int16</dd>
    </div>
    <div class="app-kv-row">
      <dt>buffer</dt>
      <dd class="app-mono">{BLOCK_SIZE} samples</dd>
    </div>
  </dl>
</section>

<style>
  .level {
    display: flex;
    gap: 2px;
    height: 10px;
  }

  .tick {
    flex: 1;
    background: var(--app-line);
    border-radius: 1px;
  }

  .tick.on {
    background: var(--app-fill);
  }
</style>
