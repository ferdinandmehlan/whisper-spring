<script lang="ts">
	import '../app.css';
	import favicon from '$lib/assets/favicon.svg';
	import LinkButton from '$lib/ui/LinkButton.svelte';
	import Button from '$lib/ui/Button.svelte';
	import ThemeToggle from '$lib/components/ThemeToggle.svelte';

	let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="min-h-screen bg-background text-foreground">
	<div class="flex min-h-screen">
		<aside class="flex w-72 flex-col bg-surface">
			<div class="flex h-16 items-center border-b border-border px-6">
				<h1
					class="font-title ml-2 bg-linear-to-r from-accent to-primary bg-clip-text text-2xl font-bold text-transparent"
				>
					Whisper Spring
				</h1>
			</div>

			<nav class="flex-1 space-y-2 overflow-y-auto p-4 pt-8">
				<LinkButton href="/transcriptions" icon="speech_to_text">Transcriptions</LinkButton>
				<LinkButton href="/live" icon="mic">Live</LinkButton>
				<LinkButton href="/models" icon="imagesmode">Models</LinkButton>
			</nav>

			<div class="p-4">
				<LinkButton href="/settings" icon="settings">Settings</LinkButton>
			</div>
		</aside>

		<div class="flex flex-1 flex-col">
			<header class="sticky top-0 z-30 h-16 backdrop-blur-lg">
				<div class="flex h-full items-center justify-end px-6">
					<div class="flex items-center gap-2">
						<Button variant="text" icon="notifications" aria-label="Notifications" />
						<Button variant="text" icon="help" aria-label="Help" />
						<ThemeToggle />
					</div>
				</div>
			</header>

			<main class="flex-1 overflow-auto p-6">
				{@render children()}
			</main>

			<footer class="px-6 py-4">
				<p class="text-center text-sm text-secondary">
					© 2026 Whisper Spring. Powered by Whisper.cpp.
				</p>
			</footer>
		</div>
	</div>
</div>
