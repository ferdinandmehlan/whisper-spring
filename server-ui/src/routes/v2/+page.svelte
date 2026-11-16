<script lang="ts">
  import Shell from '#lib/v2/Shell.svelte';
  import NavRail from '#lib/v2/NavRail.svelte';
  import Inspector from '#lib/v2/mock/Inspector.svelte';
  import TranscribePane from '#lib/v2/mock/TranscribePane.svelte';
  import LivePane from '#lib/v2/mock/LivePane.svelte';
  import ModelsPane from '#lib/v2/mock/ModelsPane.svelte';
  import SettingsPane from '#lib/v2/mock/SettingsPane.svelte';

  type View = 'transcribe' | 'live' | 'models' | 'settings';
  type Mode = 'dark' | 'light';

  let view = $state<View>('transcribe');
  let mode = $state<Mode>('dark');
</script>

<svelte:head>
  <title>whisper spring · v2 mock</title>
</svelte:head>

<Shell bind:mode>
  {#snippet left()}
    <NavRail bind:view bind:mode />
  {/snippet}

  {#snippet main()}
    {#if view === 'transcribe'}
      <TranscribePane />
    {:else if view === 'live'}
      <LivePane />
    {:else if view === 'models'}
      <ModelsPane />
    {:else}
      <SettingsPane />
    {/if}
  {/snippet}

  {#snippet right()}
    <Inspector {view} />
  {/snippet}
</Shell>
