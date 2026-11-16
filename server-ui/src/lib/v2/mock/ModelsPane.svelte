<script lang="ts">
  import Icon from '../Icon.svelte';
  import WorkArea from '../WorkArea.svelte';

  const models = [
    { name: 'ggml-tiny.en', size: '75 MB', note: 'fastest · english only', state: 'active' },
    { name: 'ggml-base.en', size: '142 MB', note: 'balanced · english only', state: 'local' },
    { name: 'ggml-small.en', size: '466 MB', note: 'accurate · english only', state: 'local' },
    { name: 'ggml-medium.en', size: '1.5 GB', note: 'heavy · english only', state: 'remote' },
    { name: 'ggml-large-v3', size: '3.1 GB', note: 'best quality · multilingual', state: 'remote' },
    { name: 'ggml-large-v3-turbo', size: '1.6 GB', note: 'fast + multilingual', state: 'remote' }
  ];
</script>

<WorkArea crumb="models" statusLeft="2 local · 4 remote" statusRight="2.4 GB stored">
  {#snippet actions()}
    <button class="v2-btn v2-btn--ghost">
      <Icon name="search" size={14} />
      Search
    </button>
  {/snippet}

  <div class="disk">
    <div class="disk-head">
      <span class="v2-eyebrow">model storage</span>
      <span class="disk-value v2-mono">2.4 GB <span class="faint">/ 40 GB</span></span>
    </div>
    <div class="meter"><span></span></div>
  </div>

  <div class="table-head v2-eyebrow">
    <span>model</span>
    <span class="col-size">size</span>
    <span class="col-state">status</span>
    <span></span>
  </div>

  <ul class="list">
    {#each models as m (m.name)}
      <li class="row" class:active={m.state === 'active'}>
        <div class="col-name">
          <span class="name v2-mono">{m.name}</span>
          <span class="note">{m.note}</span>
        </div>
        <span class="col-size v2-mono">{m.size}</span>
        <span class="col-state">
          {#if m.state === 'active'}
            <span class="v2-chip v2-chip--solid">active</span>
          {:else if m.state === 'local'}
            <span class="v2-chip">local</span>
          {:else}
            <span class="v2-chip">remote</span>
          {/if}
        </span>
        <span class="col-action">
          {#if m.state === 'active'}
            <button class="v2-icon-btn" aria-label={`Eject ${m.name}`}
              ><Icon name="stop" size={13} /></button
            >
          {:else if m.state === 'local'}
            <button class="v2-icon-btn" aria-label={`Remove ${m.name}`}
              ><Icon name="close" size={13} /></button
            >
          {:else}
            <button class="v2-icon-btn" aria-label={`Download ${m.name}`}
              ><Icon name="download" size={13} /></button
            >
          {/if}
        </span>
      </li>
    {/each}
  </ul>
</WorkArea>

<style>
  .disk {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding-bottom: 22px;
  }

  .disk-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .disk-value {
    font-size: 11px;
    color: var(--v2-text-2);
  }

  .faint {
    color: var(--v2-text-3);
  }

  .meter {
    height: 2px;
    background: var(--v2-line);
  }

  .meter span {
    display: block;
    width: 6%;
    height: 100%;
    background: var(--v2-fill);
  }

  .table-head {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 70px 78px 30px;
    gap: 12px;
    padding: 0 8px 8px;
    border-bottom: 1px solid var(--v2-line);
  }

  .list {
    display: flex;
    flex-direction: column;
  }

  .row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 70px 78px 30px;
    align-items: center;
    gap: 12px;
    padding: 10px 8px;
    border-bottom: 1px solid var(--v2-line);
    transition: background-color var(--v2-dur) var(--v2-ease);
  }

  .row:hover {
    background: var(--v2-hover);
  }

  .row.active {
    background: var(--v2-pane-alt);
  }

  .col-name {
    display: flex;
    min-width: 0;
    flex-direction: column;
    gap: 2px;
  }

  .name {
    font-size: 11.5px;
    letter-spacing: -0.01em;
  }

  .note {
    font-size: 11px;
    color: var(--v2-text-3);
  }

  .col-size {
    font-size: 10.5px;
    color: var(--v2-text-2);
  }

  .col-state {
    justify-self: start;
  }

  .col-action {
    justify-self: end;
  }

  @media (max-width: 760px) {
    .table-head,
    .row {
      grid-template-columns: minmax(0, 1fr) 78px 30px;
    }

    .col-size {
      display: none;
    }
  }
</style>
