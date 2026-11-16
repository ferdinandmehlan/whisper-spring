<script lang="ts">
  import WorkArea from '#/lib/layout/WorkArea.svelte';
  import { useInspector } from '#/lib/layout/inspector.svelte';
  import { formatBytes } from '#/lib/components/format';
  import { models } from '#/lib/models/models.svelte';
  import StorageHead from '#/lib/models/StorageHead.svelte';
  import CatalogError from '#/lib/models/CatalogError.svelte';
  import CatalogTable from '#/lib/models/CatalogTable.svelte';
  import SwitchingNote from '#/lib/models/SwitchingNote.svelte';
  import ModelsSection from '#/lib/models/ModelsSection.svelte';

  const modelCount = $derived(models.list.length);
  const count = $derived(modelCount === 1 ? '1 model' : `${modelCount} models`);
  useInspector('details', inspector);
</script>

{#snippet inspector()}
  <ModelsSection {modelCount} />
{/snippet}

<WorkArea
  crumb="models"
  statusLeft={`${count} · /api/models`}
  statusRight={models.status === 'ready'
    ? `${formatBytes(models.totalBytes)} on disk`
    : 'not ready'}
>
  <StorageHead />

  {#if models.status === 'error'}
    <CatalogError />
  {:else}
    <CatalogTable />
  {/if}

  <SwitchingNote />
</WorkArea>
