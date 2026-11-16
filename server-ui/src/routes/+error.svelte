<script lang="ts">
  import { resolve } from '$app/paths';
  import { page } from '$app/state';
  import '#/lib/styles/tokens.css';

  const status = $derived(page.status);
  const eyebrow = $derived(status === 404 ? '404 · not found' : `${status} · server error`);
  const heading = $derived(
    status === 404 ? 'The page you are looking for does not exist.' : 'This page could not load.'
  );
  const body = $derived(
    status === 404
      ? 'It may have been moved, or the link may be out of date.'
      : 'Reloading sometimes helps. If it keeps happening, the server log has the details.'
  );
</script>

<svelte:head><title>whisper spring · {status === 404 ? 'not found' : 'error'}</title></svelte:head>

<main class="error">
  <div class="card">
    <span class="eyebrow">{eyebrow}</span>
    <h1>{heading}</h1>
    <p>{body}</p>
    <a href={resolve('')}>Go to the workspace</a>
  </div>
</main>

<style>
  .error {
    --error-bg: #0b0b0c;
    --error-card: #141416;
    --error-line: rgb(255 255 255 / 10%);
    --error-text: #f4f4f5;
    --error-muted: #a2a2aa;
    --error-faint: #8b8b93;
    --error-action-bg: #f4f4f5;
    --error-action-text: #0b0b0c;
    --error-action-hover: #fff;

    display: grid;
    min-height: 100dvh;
    padding: 24px;
    place-items: center;
    background: var(--error-bg);
    color: var(--error-text);
    font-family:
      ui-sans-serif,
      system-ui,
      -apple-system,
      'Segoe UI',
      Roboto,
      'Helvetica Neue',
      Arial,
      sans-serif;
  }

  :global(:root[data-theme='light']) .error {
    --error-bg: #e9e9eb;
    --error-card: #fff;
    --error-line: rgb(0 0 0 / 10%);
    --error-text: #101012;
    --error-muted: #55555e;
    --error-faint: #6c6c75;
    --error-action-bg: #101012;
    --error-action-text: #fff;
    --error-action-hover: #000;
  }

  .card {
    display: flex;
    max-width: 42ch;
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
    padding: 32px;
    background: var(--error-card);
    border: 1px solid var(--error-line);
    border-radius: 14px;
  }

  .eyebrow {
    color: var(--error-faint);
    font-family: ui-monospace, 'SF Mono', Menlo, monospace;
    font-size: var(--app-fs-2xs);
    font-weight: 500;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  h1 {
    margin: 0;
    font-size: var(--app-fs-2xl);
    font-weight: 600;
    letter-spacing: -0.01em;
    line-height: 1.25;
  }

  p {
    margin: 0;
    color: var(--error-muted);
    font-size: var(--app-fs-lg);
    line-height: 1.5;
  }

  a {
    margin-top: 6px;
    padding: 8px 14px;
    color: var(--error-action-text);
    font-size: var(--app-fs-md);
    font-weight: 500;
    text-decoration: none;
    background: var(--error-action-bg);
    border-radius: 7px;
  }

  a:hover {
    background: var(--error-action-hover);
  }
</style>
