<script lang="ts">
  import Icon from '#/lib/components/Icon.svelte';
  import WorkArea from '#/lib/layout/WorkArea.svelte';
  import { useInspector } from '#/lib/layout/inspector.svelte';
  import { live } from '#/lib/live/live.svelte';
  import MetaBar from '#/lib/live/MetaBar.svelte';
  import WaveStrip from '#/lib/live/WaveStrip.svelte';
  import Transport from '#/lib/live/Transport.svelte';
  import StreamList from '#/lib/live/StreamList.svelte';
  import LiveSection from '#/lib/live/LiveSection.svelte';

  useInspector('input', inspector);
</script>

{#snippet inspector()}
  <LiveSection />
{/snippet}

<WorkArea crumb="live" statusLeft={`${live.phase} · /api/live`}>
  <div class="stage">
    <MetaBar />
    <WaveStrip />
    <Transport />

    {#if live.error}
      <div class="error" role="alert">
        <Icon name="alert" size={15} />
        <p>{live.error}</p>
      </div>
    {/if}

    <StreamList />
  </div>
</WorkArea>

<style>
  .stage {
    display: flex;
    min-height: 100%;
    flex-direction: column;
  }

  .error {
    display: flex;
    gap: 10px;
    margin-top: 14px;
    padding: 12px 14px;
    font-size: var(--app-fs-md);
    line-height: 1.5;
    background: color-mix(in srgb, var(--app-danger) 8%, transparent);
    border: 1px solid color-mix(in srgb, var(--app-danger) 45%, transparent);
    border-radius: var(--app-radius-sm);
  }

  .error :global(svg) {
    flex: none;
    margin-top: 2px;
    color: var(--app-danger);
  }
</style>
