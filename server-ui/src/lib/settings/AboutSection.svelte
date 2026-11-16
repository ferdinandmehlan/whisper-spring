<script lang="ts">
  import { SOURCE, SOURCE_LABEL } from '#/lib/settings/about';
</script>

<section id="about" class="app-group">
  <h3 class="app-eyebrow">about</h3>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Whisper Spring</span>
      <span class="app-row-desc">Local transcription over whisper.cpp, served by Spring Boot.</span>
    </div>
    <div class="app-row-control">
      <!-- The version and nothing else: the trailing readout names the build,
           the same one the rail prints under the wordmark. "islands" was the
           theme's name, which nobody can act on from this screen. -->
      <span class="app-mono app-value">v{__APP_VERSION__}</span>
    </div>
  </div>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Source</span>
      <span class="app-row-desc">Code, issues and releases.</span>
    </div>
    <div class="app-row-control">
      <a
        class="source app-mono"
        href={SOURCE}
        target="_blank"
        rel="external noreferrer"
        title={SOURCE}
      >
        {SOURCE_LABEL}
      </a>
    </div>
  </div>

  <div class="app-row">
    <div class="app-row-label">
      <span class="app-row-title">Preferences</span>
      <span class="app-row-desc">Kept in this browser; the server stores none of them.</span>
    </div>
    <div class="app-row-control">
      <span class="app-mono app-value">localStorage</span>
    </div>
  </div>
</section>

<style>
  /* A value that happens to be a link: same size and colour as the readouts
     beside it, underlined in the line colour so the row still reads as a row
     and the affordance shows up on hover. */
  .source {
    font-size: var(--app-fs-sm);
    color: var(--app-text-2);
    text-decoration: underline;
    text-decoration-color: var(--app-line-strong);
    text-underline-offset: 3px;
    white-space: nowrap;
  }

  .source:hover,
  .source:focus-visible {
    color: var(--app-text);
    text-decoration-color: currentColor;
  }
</style>
