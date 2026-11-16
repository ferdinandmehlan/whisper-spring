<script lang="ts">
  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { resolve } from '$app/paths';
  import favicon from '#/lib/assets/favicon.svg';
  import { viewForKey } from '#/lib/layout/NavRail.svelte';
  import { provideInspector } from '#/lib/layout/inspector.svelte';
  import { models } from '#/lib/models/models.svelte';
  import { options } from '#/lib/transcriptions/options.svelte';
  import { prefs } from '#/lib/prefs.svelte';
  import '#/app.css';
  import Inspector from '#/lib/layout/Inspector.svelte';
  import NavRail from '#/lib/layout/NavRail.svelte';
  import Shell from '#/lib/layout/Shell.svelte';

  let { children } = $props();

  const inspector = provideInspector();

  onMount(() => {
    prefs.hydrate();
    options.hydrate();
    void models.load();
  });

  function isTyping(target: EventTarget | null) {
    return (
      target instanceof HTMLElement &&
      !!target.closest('input, textarea, select, [contenteditable="true"]')
    );
  }

  function onkeydown(event: KeyboardEvent) {
    if (event.metaKey || event.ctrlKey || event.altKey || isTyping(event.target)) return;

    const target = viewForKey(event.key);
    if (target) {
      event.preventDefault();
      void goto(resolve(target));
      return;
    }

    if (event.key === '[') prefs.toggleLeft();
    else if (event.key === ']') prefs.toggleRight();
    else if (event.key === '\\') prefs.toggleAll();
    else return;

    event.preventDefault();
  }
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<svelte:window {onkeydown} />

<Shell
  bind:leftCollapsed={prefs.leftCollapsed}
  bind:rightCollapsed={prefs.rightCollapsed}
  rightLabel={() => inspector.current?.title ?? 'Inspector'}
>
  {#snippet left()}
    <NavRail oncollapse={() => (prefs.leftCollapsed = true)} />
  {/snippet}

  {#snippet main()}
    {@render children()}
  {/snippet}

  {#snippet right()}
    <Inspector inspector={inspector.current} oncollapse={() => (prefs.rightCollapsed = true)} />
  {/snippet}
</Shell>
