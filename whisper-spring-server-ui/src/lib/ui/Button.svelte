<script lang="ts">
	import type { Snippet } from 'svelte';
	import { twMerge } from 'tailwind-merge';
	import Icon from './Icon.svelte';

	type ButtonVariant = 'filled' | 'outlined' | 'text';

	type ButtonProps = {
		variant?: ButtonVariant;
		icon?: string;
		children?: Snippet;
		disabled?: boolean;
		onclick?: (event: MouseEvent) => void;
		type?: 'button' | 'submit' | 'reset';
		class?: string;
		'aria-label'?: string;
	};

	let {
		variant = 'filled',
		icon,
		children,
		disabled = false,
		onclick,
		type = 'button',
		class: className = '',
		'aria-label': ariaLabel
	}: ButtonProps = $props();

	const baseClasses =
		'inline-flex items-center justify-center gap-2 rounded-lg p-2 font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50';

	const variantClasses: Record<ButtonVariant, string> = {
		filled: 'bg-primary text-foreground enabled:hover:bg-primary-hover',
		outlined: 'border border-primary text-primary enabled:hover:bg-primary/10',
		text: 'text-foreground enabled:hover:bg-primary/10'
	};

	let mergedClasses = $derived(twMerge(baseClasses, variantClasses[variant], className));
</script>

<button {type} {disabled} {onclick} aria-label={ariaLabel} class={mergedClasses}>
	{#if icon}
		<Icon {icon} />
	{/if}
	{#if children}
		{@render children()}
	{/if}
</button>
