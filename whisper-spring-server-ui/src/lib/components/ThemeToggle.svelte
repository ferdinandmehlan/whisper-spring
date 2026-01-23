<script lang="ts">
	import Button from '$lib/ui/Button.svelte';
	import { onMount } from 'svelte';

	type ThemeToggleProps = {
		class?: string;
	};

	let { class: className = '' }: ThemeToggleProps = $props();
	let isDark = $state(false);

	function getInitialTheme(): boolean {
		const stored = sessionStorage.getItem('theme');
		if (stored) {
			return stored === 'dark';
		}
		return window.matchMedia('(prefers-color-scheme: dark)').matches;
	}

	function applyTheme(dark: boolean) {
		document.documentElement.classList.toggle('dark', dark);
	}

	function toggleDarkMode() {
		isDark = !isDark;
		sessionStorage.setItem('theme', isDark ? 'dark' : 'light');
		applyTheme(isDark);
	}

	onMount(() => {
		isDark = getInitialTheme();
		applyTheme(isDark);
	});
</script>

<Button
	variant="text"
	icon={isDark ? 'dark_mode' : 'light_mode'}
	aria-label="Toggle dark mode"
	onclick={toggleDarkMode}
	class={className}
/>
