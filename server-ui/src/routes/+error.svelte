<script lang="ts">
  import { resolve } from '$app/paths';
</script>

<div class="error">
  <h1 class="code">404</h1>
  <p class="intro">Page not found</p>
  <p class="hint">The page you are looking for doesn't exist or has been moved.</p>
  <a href={resolve('/')} class="btn btn--filled ripple home">Go home</a>
</div>

<style>
  .error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: var(--space-4);
    min-height: 60vh;
    text-align: center;
  }

  .code {
    font-size: var(--text-6xl);
    line-height: var(--leading-6xl);
    font-weight: var(--weight-bold);
  }

  .intro {
    font-size: var(--text-xl);
    line-height: var(--leading-xl);
    color: var(--color-secondary);
  }

  .hint {
    font-size: var(--text-sm);
    line-height: var(--leading-sm);
    color: var(--color-secondary);
  }

  .home {
    /* The old markup had both a `space-y-4` gap and `mt-4` here, so this keeps the 32px gap. */
    margin-top: var(--space-4);
    padding-inline: var(--space-6);
  }
</style>
