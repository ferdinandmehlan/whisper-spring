<script lang="ts">
  import '../app.css';
  import favicon from '#lib/assets/favicon.svg';
  import LinkButton from '#lib/ui/LinkButton.svelte';
  import Button from '#lib/ui/Button.svelte';
  import ThemeToggle from '#lib/components/ThemeToggle.svelte';

  let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="shell">
  <aside class="sidebar">
    <div class="brand">
      <h1 class="brand-title rail-label">Whisper Spring</h1>
    </div>
    <nav class="nav">
      <div class="nav-main">
        <LinkButton href="/transcriptions" icon="speech_to_text">Transcriptions</LinkButton>
        <LinkButton href="/live" icon="mic">Live</LinkButton>
        <LinkButton href="/models" icon="imagesmode">Models</LinkButton>
      </div>

      <div class="nav-footer">
        <LinkButton href="/settings" icon="settings">Settings</LinkButton>
      </div>
    </nav>
  </aside>

  <div id="scroll" class="scroll-area scrollbar-hide">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="topbar-actions">
          <Button variant="text" icon="notifications" aria-label="Notifications" />
          <Button variant="text" icon="help" aria-label="Help" />
          <ThemeToggle />
        </div>
      </div>
    </header>

    <main class="content">
      {@render children()}
    </main>
  </div>
</div>

<style>
  .shell {
    display: flex;
    /* dvh keeps the shell correct when mobile browser chrome shows/hides */
    height: 100dvh;
    background-color: var(--color-background);
    color: var(--color-foreground);
  }

  .sidebar {
    display: flex;
    flex: none;
    flex-direction: column;
    width: var(--sidebar-width);
    background-color: var(--color-surface);
  }

  .brand {
    display: flex;
    align-items: center;
    height: var(--header-height);
    padding: 0 var(--space-6);
    border-bottom: 1px solid var(--color-border);
  }

  .brand-title {
    margin-left: var(--space-2);
    background-image: linear-gradient(to right, var(--color-accent), var(--color-primary));
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
  }

  .nav {
    display: flex;
    flex: 1;
    flex-direction: column;
  }

  .nav-main {
    flex: 1;
    padding: var(--space-4);
    padding-top: var(--space-8);
  }

  .nav-footer {
    padding: var(--space-4);
  }

  .scroll-area {
    display: flex;
    /* min-width: 0 lets this flex item shrink below its content width instead
       of pushing the layout into horizontal scrolling. */
    min-width: 0;
    flex: 1;
    flex-direction: column;
    overflow-y: auto;
  }

  .topbar {
    position: sticky;
    top: 0;
    z-index: var(--z-sticky);
    display: flex;
    height: var(--topbar-height);
    background-color: transparent;
    backdrop-filter: blur(var(--blur-lg));
  }

  .topbar-inner {
    display: flex;
    flex: 1;
    justify-content: flex-end;
    padding: var(--space-4);
    padding-right: var(--space-6);
  }

  .topbar-actions {
    display: flex;
    gap: var(--space-2);
  }

  .content {
    display: flex;
    min-width: 0;
    flex: 1;
  }

  /*
   * Below 60rem the sidebar becomes an icon rail. The width lives in a token so
   * the rest of the shell does not need to know about the breakpoint, and the
   * labels are only hidden visually (`.rail-label`) so links keep their names.
   * The breakpoint is a literal: custom properties cannot be used in media
   * queries.
   */
  @media (max-width: 60rem) {
    .sidebar {
      width: var(--sidebar-width-rail);
    }

    .brand {
      justify-content: center;
      padding: 0;
    }

    /* Keep the rail links at a comfortable hit target (56x48). */
    .nav-main,
    .nav-footer {
      padding-inline: var(--space-2);
    }
  }
</style>
