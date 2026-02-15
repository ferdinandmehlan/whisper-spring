<script lang="ts">
	import { resolve } from '$app/paths';
	import type { RouteId } from '$app/types';
	import type { Snippet } from 'svelte';
	import { twMerge } from 'tailwind-merge';
	import Icon from './Icon.svelte';

	type LinkButtonProps = {
		href: RouteId;
		icon?: string;
		children?: Snippet;
		class?: string;
	};

	let { href, icon, children, class: className = '' }: LinkButtonProps = $props();
</script>

<a
	href={resolve(href)}
	class={twMerge(
		'group flex items-center gap-3 rounded-xl px-4 py-3 text-secondary transition-all hover:bg-secondary/10 hover:text-foreground',
		className
	)}
>
	{#if icon}
		<Icon {icon} />
	{/if}
	{#if children}
		<span class="font-medium">{@render children()}</span>
	{/if}
</a>
