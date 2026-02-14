<script lang="ts">
	import { twMerge } from 'tailwind-merge';

	type IconProps = {
		icon: string;
		class?: string;
	};

	let { icon, class: className = '' }: IconProps = $props();

	let mergedClasses = $derived(twMerge('material-symbols-outlined', className));
</script>

<span class={mergedClasses}>
	{icon}
</span>
