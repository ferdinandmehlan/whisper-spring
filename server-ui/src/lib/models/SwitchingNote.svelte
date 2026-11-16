<section class="switching">
  <span class="app-eyebrow">switching</span>
  <p class="app-hint hint">
    The model is loaded once, at startup. Picking a different one needs a load endpoint on the
    server, so no control here pretends to do it yet.
  </p>
</section>

<style>
  .switching {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding-top: 22px;
  }

  .hint {
    max-width: 76ch;
  }
</style>
