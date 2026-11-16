<script module lang="ts">
  type View = 'transcription' | 'live' | 'models' | 'settings';

  interface ViewSpec {
    label: string;
    icon: string;
    key: string;
    footer?: boolean;
  }

  const VIEWS: Record<View, ViewSpec> = {
    transcription: { label: 'Transcription', icon: 'transcribe', key: '1' },
    live: { label: 'Live', icon: 'mic', key: '2' },
    models: { label: 'Models', icon: 'models', key: '3' },
    settings: { label: 'Settings', icon: 'settings', key: '0', footer: true }
  };
  const VIEW_ORDER = Object.keys(VIEWS) as View[];

  export function viewFromPath(pathname: string): View {
    const segment = pathname.split('/').filter(Boolean).at(-1);
    return VIEW_ORDER.find((view) => view === segment) ?? 'transcription';
  }

  export function viewForKey(key: string): View | undefined {
    return VIEW_ORDER.find((view) => VIEWS[view].key === key);
  }
</script>

<script lang="ts">
  import { resolve } from '$app/paths';
  import { page } from '$app/state';
  import Icon from '#/lib/components/Icon.svelte';
  import IconButton from '#/lib/components/IconButton.svelte';

  let { oncollapse }: { oncollapse?: () => void } = $props();

  const items = VIEW_ORDER.map((id) => ({ id, href: resolve(id), ...VIEWS[id] }));
  const mainItems = items.filter((item) => !item.footer);
  const footerItems = items.filter((item) => item.footer);

  type RailItem = (typeof items)[number];

  const active = $derived(viewFromPath(page.url.pathname));
</script>

<div class="rail">
  <header class="brand">
    <div class="mark">
      <Icon name="wave" size={15} stroke={2} />
    </div>
    <div class="brand-text">
      <span class="name">whisper<span class="thin"> spring</span></span>
      <span class="app-eyebrow">v{__APP_VERSION__}</span>
    </div>

    {#if oncollapse}
      <IconButton
        class="collapse"
        aria-label="Collapse workspace"
        aria-keyshortcuts="["
        title="Collapse workspace ([)"
        onclick={oncollapse}
      >
        <Icon name="panelLeft" size={13} />
      </IconButton>
    {/if}
  </header>

  {#snippet itemLink(item: RailItem)}
    <a
      class="item"
      class:active={active === item.id}
      aria-current={active === item.id ? 'page' : undefined}
      aria-keyshortcuts={item.key}
      href={item.href}
    >
      <Icon name={item.icon} size={15} />
      <span class="label">{item.label}</span>
      <kbd class="key">{item.key}</kbd>
    </a>
  {/snippet}

  <nav class="nav" aria-label="Workspace">
    {#each mainItems as item (item.id)}
      {@render itemLink(item)}
    {/each}
  </nav>

  <div class="app-hr"></div>

  <!-- Placeholder, and deliberately empty. Past runs of transcription and live
       will list here; what it must not do is show a model or a mock entry to
       look busy. The heading is the whole feature until there is history. -->
  <section class="recent" aria-labelledby="recent-head">
    <h2 class="app-eyebrow" id="recent-head">recent</h2>
  </section>

  <footer class="rail-foot">
    {#each footerItems as item (item.id)}
      {@render itemLink(item)}
    {/each}
  </footer>
</div>

<style>
  .rail {
    display: flex;
    height: 100%;
    flex-direction: column;
    padding: 0 8px;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 9px;
    height: 52px;
    padding: 0 8px;
  }

  .brand :global(.collapse) {
    margin-left: auto;
    flex: none;
  }

  .mark {
    display: grid;
    width: 26px;
    height: 26px;
    flex: none;
    place-items: center;
    color: var(--app-on-fill);
    background: var(--app-fill);
    border-radius: 7px;
  }

  .brand-text {
    display: flex;
    min-width: 0;
    flex-direction: column;
    gap: 3px;
  }

  .name {
    font-size: var(--app-fs-lg);
    font-weight: 600;
    letter-spacing: -0.02em;
    line-height: 1;
    white-space: nowrap;
  }

  .thin {
    color: var(--app-text-3);
    font-weight: 400;
  }

  .nav {
    display: flex;
    flex-direction: column;
    gap: 1px;
    padding: 6px 0 10px;
  }

  .item {
    display: flex;
    align-items: center;
    gap: 9px;
    width: 100%;
    height: 32px;
    padding: 0 8px;
    font-size: var(--app-fs-md);
    color: var(--app-text-2);
    text-align: left;
    text-decoration: none;
    background: transparent;
    border: 0;
    border-radius: var(--app-radius-sm);
    cursor: pointer;
    transition:
      background-color var(--app-dur) var(--app-ease),
      color var(--app-dur) var(--app-ease);
  }

  .item:hover {
    color: var(--app-text);
    background: var(--app-hover);
  }

  .item.active {
    color: var(--app-text);
    background: var(--app-active);
  }

  .item.active :global(svg) {
    color: var(--app-text);
  }

  .label {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .key {
    font-family: var(--app-mono);
    font-size: var(--app-fs-2xs);
    color: var(--app-text-3);
    opacity: 0;
    transition: opacity var(--app-dur) var(--app-ease);
  }

  .item:hover .key,
  .item:focus-visible .key {
    opacity: 1;
  }

  .recent {
    display: flex;
    flex: 1;
    flex-direction: column;
    gap: 8px;
    padding: 14px 8px;
  }

  .rail-foot {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 8px 0 4px;
    border-top: 1px solid var(--app-line);
  }

  @media (max-width: 1024px) {
    .brand-text,
    .label,
    .key,
    .recent {
      display: none;
    }

    .brand :global(.collapse) {
      display: none;
    }

    .rail {
      align-items: center;
      padding: 0 4px;
    }

    .brand {
      justify-content: center;
      padding: 0;
    }

    .item {
      justify-content: center;
      width: 32px;
      padding: 0;
    }

    .rail-foot {
      width: 100%;
      align-items: center;
    }
  }
</style>
