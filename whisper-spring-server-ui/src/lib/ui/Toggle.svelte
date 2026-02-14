<script lang="ts">
	import { twMerge } from 'tailwind-merge';

	type ToggleProps = {
		checked?: boolean;
		label?: string;
		disabled?: boolean;
		onchange?: (checked: boolean) => void;
		class?: string;
	};

	let {
		checked = $bindable(false),
		label,
		disabled = $bindable(false),
		onchange,
		class: className = ''
	}: ToggleProps = $props();

	function handleChange(event: Event) {
		const target = event.target as HTMLInputElement;
		checked = target.checked;
		onchange?.(checked);
	}

	const baseClasses = 'inline-flex cursor-pointer items-center gap-3';
	const disabledClasses = disabled ? 'cursor-not-allowed opacity-50' : '';
	let mergedClasses = $derived(twMerge(baseClasses, disabledClasses, className));
</script>

<label class={mergedClasses}>
	<input type="checkbox" bind:checked {disabled} onchange={handleChange} class="peer sr-only" />
	<div
		class="relative h-6 w-11 rounded-full bg-secondary/30 transition-colors peer-checked:bg-primary peer-focus:ring-2 peer-focus:ring-primary/50 peer-disabled:cursor-not-allowed after:absolute after:start-0.5 after:top-0.5 after:h-5 after:w-5 after:rounded-full after:bg-background after:transition-all peer-checked:after:translate-x-full"
	></div>
	{#if label}
		<span class="text-sm font-medium">{label}</span>
	{/if}
</label>
