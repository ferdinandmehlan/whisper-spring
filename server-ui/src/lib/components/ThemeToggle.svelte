<script lang="ts">
  import Button from '#lib/ui/Button.svelte';
  import { onMount } from 'svelte';

  // app.html already applied the stored/system theme before first paint; this
  // component only mirrors that state and lets the user override it.
  let isDark = $state(false);

  function storeTheme(dark: boolean) {
    try {
      localStorage.setItem('theme', dark ? 'dark' : 'light');
    } catch {
      // Persisting is best effort; the class below still applies for this page.
    }
  }

  function toggleDarkMode() {
    isDark = !isDark;
    document.documentElement.classList.toggle('dark', isDark);
    storeTheme(isDark);
  }

  onMount(() => {
    isDark = document.documentElement.classList.contains('dark');
  });
</script>

<Button
  variant="text"
  icon={isDark ? 'dark_mode' : 'light_mode'}
  aria-label="Toggle dark mode"
  onclick={toggleDarkMode}
/>
