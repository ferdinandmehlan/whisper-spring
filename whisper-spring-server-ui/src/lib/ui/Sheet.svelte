<script lang="ts">
	import type { Snippet } from 'svelte';
	import { twMerge } from 'tailwind-merge';

	type SheetProps = {
		title?: string;
		children?: Snippet;
		class?: string;
	};

	let { title, children, class: className = '' }: SheetProps = $props();
</script>

<div class={twMerge('rounded-xl bg-surface p-6 shadow-md', className)}>
	{#if title}
		<h3 class="mb-4 text-lg font-semibold">{title}</h3>
	{/if}
	{#if children}
		{@render children()}
	{/if}
</div>
