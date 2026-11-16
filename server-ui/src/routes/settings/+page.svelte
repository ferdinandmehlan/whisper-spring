<script lang="ts">
  import WorkArea from '#/lib/layout/WorkArea.svelte';
  import { useInspector } from '#/lib/layout/inspector.svelte';
  import AboutSection from '#/lib/settings/AboutSection.svelte';
  import AppearanceSection from '#/lib/settings/AppearanceSection.svelte';
  import ServerSection from '#/lib/settings/ServerSection.svelte';
  import SettingsSection from '#/lib/settings/SettingsSection.svelte';

  useInspector('sections', inspector);
</script>

{#snippet inspector()}
  <SettingsSection />
{/snippet}

<WorkArea
  crumb="settings"
  statusLeft="stored in this browser"
  statusRight="localStorage · whisper-spring-settings"
>
  <div class="cols">
    <ServerSection />
    <AppearanceSection />
    <AboutSection />
  </div>
</WorkArea>

<style>
  .cols {
    display: flex;
    flex-direction: column;
    gap: 26px;
  }
</style>
