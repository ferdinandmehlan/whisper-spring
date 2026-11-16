<script lang="ts">
  import { SOURCE, SOURCE_LABEL } from '#/lib/settings/about';
</script>

<section class="app-block" role="group" aria-labelledby="settings-about">
  <h3 class="app-eyebrow" id="settings-about">about</h3>
  <dl class="app-kv">
    <div class="app-kv-row">
      <dt>version</dt>
      <dd class="app-mono">v{__APP_VERSION__}</dd>
    </div>
    <div class="app-kv-row">
      <dt>source</dt>
      <dd class="app-mono">
        <a class="source" href={SOURCE} target="_blank" rel="external noreferrer" title={SOURCE}>
          {SOURCE_LABEL}
        </a>
      </dd>
    </div>
  </dl>
</section>

<style>
  .source {
    color: var(--app-text-2);
    text-decoration: underline;
    text-decoration-color: var(--app-line-strong);
    text-underline-offset: 3px;
    overflow-wrap: anywhere;
  }

  .source:hover,
  .source:focus-visible {
    color: var(--app-text);
    text-decoration-color: currentColor;
  }
</style>
