<script lang="ts">
  import { prefs, type Mode } from '#/lib/prefs.svelte';
  import Segmented from '#/lib/components/Segmented.svelte';

  const THEMES: { value: Mode; label: string }[] = [
    { value: 'dark', label: 'dark' },
    { value: 'light', label: 'light' }
  ];
</script>

<section id="appearance" class="app-group">
  <h3 class="app-eyebrow">appearance</h3>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Theme</span>
      <span class="app-row-desc">Choose from dark and light theme.</span>
    </div>
    <div class="app-row-control">
      <Segmented
        label="Theme"
        segments={THEMES}
        active={prefs.mode}
        onselect={(value) => prefs.setMode(value)}
      />
    </div>
  </div>
</section>
