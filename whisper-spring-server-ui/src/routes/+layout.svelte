<script lang="ts">
  import '../app.css';
  import favicon from '$lib/assets/favicon.svg';
  import LinkButton from '$lib/ui/LinkButton.svelte';
  import Button from '$lib/ui/Button.svelte';
  import ThemeToggle from '$lib/components/ThemeToggle.svelte';

  let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="flex h-screen bg-background text-foreground">
  <aside class="flex w-72 flex-col bg-surface">
    <div class="flex h-16 items-center border-b border-border px-6">
      <h1 class="ml-2 bg-linear-to-r from-accent to-primary bg-clip-text text-transparent">
        Whisper Spring
      </h1>
    </div>
    <nav class="flex flex-1 flex-col">
      <div class="flex-1 p-4 pt-8">
        <LinkButton href="/transcriptions" icon="speech_to_text">Transcriptions</LinkButton>
        <LinkButton href="/live" icon="mic">Live</LinkButton>
        <LinkButton href="/models" icon="imagesmode">Models</LinkButton>
      </div>

      <div class="p-4">
        <LinkButton href="/settings" icon="settings">Settings</LinkButton>
      </div>
    </nav>
  </aside>

  <div id="scroll" class="scrollbar-hide flex flex-1 flex-col overflow-y-auto">
    <header class="sticky top-0 z-30 flex h-20 bg-transparent backdrop-blur-lg">
      <div class="flex flex-1 justify-end p-4 pr-6">
        <div class="flex gap-2">
          <Button variant="text" icon="notifications" aria-label="Notifications" />
          <Button variant="text" icon="help" aria-label="Help" />
          <ThemeToggle />
        </div>
      </div>
    </header>

    <main class="flex flex-1">
      {@render children()}
    </main>
  </div>
</div>
