<script lang="ts">
  import type { Snippet } from 'svelte';
  import { twMerge } from 'tailwind-merge';
  import Button from './Button.svelte';

  type SidebarProps = {
    open: boolean;
    onClose: () => void;
    title?: string;
    class?: string;
    children?: Snippet;
  };

  let { open, onClose, title, class: className = '', children }: SidebarProps = $props();

  $effect(() => {
    if (!open) return;

    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape') {
        onClose();
      }
    }

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  });
</script>

{#if open}
  <div
    class="fixed inset-0 z-50 flex items-center justify-end bg-black/30"
    onclick={onClose}
    role="presentation"
    tabindex="-1"
  >
    <div
      class={twMerge('h-full w-80 overflow-y-auto bg-surface p-4 shadow-lg', className)}
      role="dialog"
      aria-modal="true"
      tabindex="0"
      onclick={(e) => e.stopPropagation()}
      onkeydown={(e) => e.stopPropagation()}
    >
      {#if title}
        <div class="mb-4 flex items-center justify-between">
          <h3 class="text-lg font-semibold">{title}</h3>
          <Button onclick={onClose} aria-label="Close" icon="close" variant="text" />
        </div>
      {/if}

      {#if children}
        {@render children()}
      {/if}
    </div>
  </div>
{/if}
