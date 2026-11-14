<script lang="ts">
  import type { Snippet } from 'svelte';
  import Button from './Button.svelte';

  type SidebarProps = {
    open: boolean;
    onClose: () => void;
    title?: string;
    children?: Snippet;
  };

  let { open, onClose, title, children }: SidebarProps = $props();

  let dialog: HTMLDialogElement | undefined = $state();
  const titleId = $props.id();

  // A native <dialog> in modal mode provides the focus trap, Esc handling, the
  // inert background and top-layer stacking, so none of that is hand-rolled.
  $effect(() => {
    if (!dialog) return;

    if (open && !dialog.open) {
      dialog.showModal();
    } else if (!open && dialog.open) {
      dialog.close();
    }
  });

  // showModal() makes the background inert but still scrollable with the wheel.
  $effect(() => {
    if (!open) return;

    const { style } = document.body;
    const previous = style.overflow;
    style.overflow = 'hidden';

    return () => {
      style.overflow = previous;
    };
  });
</script>

<dialog
  bind:this={dialog}
  class="panel"
  aria-labelledby={title ? titleId : undefined}
  onclose={onClose}
  onclick={(event) => {
    // Clicks on the panel itself mean the backdrop was hit
    if (event.target === dialog) onClose();
  }}
>
  {#if title}
    <div class="header">
      <h3 class="title" id={titleId}>{title}</h3>
      <Button onclick={onClose} aria-label="Close" icon="close" variant="text" />
    </div>
  {/if}

  {#if children}
    {@render children()}
  {/if}
</dialog>

<style>
  .panel {
    width: var(--panel-width);
    max-width: 100%;
    height: 100%;
    max-height: 100%;
    margin: 0 0 0 auto;
    padding: var(--space-4);
    overflow-y: auto;
    background-color: var(--color-surface);
    box-shadow: var(--shadow-lg);
  }

  .panel::backdrop {
    background-color: rgb(0 0 0 / 0.3);
  }

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: var(--space-4);
  }

  .title {
    font-size: var(--text-lg);
    line-height: var(--leading-lg);
    font-weight: var(--weight-semibold);
  }
</style>
