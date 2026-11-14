<script lang="ts">
  type PlaceholderProps = {
    title: string;
    message: string;
  };

  let { title, message }: PlaceholderProps = $props();
</script>

<div class="page placeholder">
  <h2>{title}</h2>
  <div class="card">
    <p>{message}</p>
  </div>
</div>

<style>
  .placeholder {
    gap: var(--space-6);
  }

  .card {
    padding: var(--space-8);
    text-align: center;
  }

  .card p {
    color: var(--color-secondary);
  }
</style>
