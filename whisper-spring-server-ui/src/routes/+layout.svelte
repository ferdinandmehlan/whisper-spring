<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';

	let { children } = $props();

	let sidebarOpen = $state(false);
	let isDark = $state(false);

	function toggleSidebar() {
		sidebarOpen = !sidebarOpen;
	}

	function toggleDarkMode() {
		isDark = !isDark;
		document.documentElement.classList.toggle('dark', isDark);
	}
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="min-h-screen bg-background text-foreground">
	<div class="flex min-h-screen">
		{#if sidebarOpen}
			<button
				class="fixed inset-0 z-50 cursor-pointer bg-black/50 backdrop-blur-sm transition-opacity lg:hidden"
				onclick={toggleSidebar}
				aria-label="Close sidebar"
			></button>
		{/if}

		<aside
			class="fixed inset-y-0 left-0 z-50 w-72 border-r border-border bg-surface transition-transform duration-300 ease-in-out lg:static lg:inset-0 lg:flex lg:translate-x-0 lg:flex-col"
			class:translate-x-0={sidebarOpen}
		>
			<div class="flex h-16 items-center justify-between border-b border-border px-6">
				<h1
					class="bg-gradient-to-r from-primary to-primary-light bg-clip-text text-xl font-bold text-transparent"
				>
					Whisper Spring
				</h1>
				<button
					onclick={toggleSidebar}
					class="rounded-lg p-2 transition-colors hover:bg-secondary/10 lg:hidden"
					aria-label="Close sidebar"
				>
					<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						/>
					</svg>
				</button>
			</div>

			<nav class="flex-1 space-y-2 overflow-y-auto px-4 py-6">
				<a
					href="/"
					class="group flex items-center gap-3 rounded-xl px-4 py-3 text-secondary transition-all hover:bg-secondary/10 hover:text-foreground"
				>
					<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
						/>
					</svg>
					<span class="font-medium">Dashboard</span>
				</a>

				<a
					href="/transcriptions"
					class="group flex items-center gap-3 rounded-xl px-4 py-3 text-secondary transition-all hover:bg-secondary/10 hover:text-foreground"
				>
					<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z"
						/>
					</svg>
					<span class="font-medium">Transcriptions</span>
				</a>

				<a
					href="/models"
					class="group flex items-center gap-3 rounded-xl px-4 py-3 text-secondary transition-all hover:bg-secondary/10 hover:text-foreground"
				>
					<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
						/>
					</svg>
					<span class="font-medium">Models</span>
				</a>

				<a
					href="/settings"
					class="group flex items-center gap-3 rounded-xl px-4 py-3 text-secondary transition-all hover:bg-secondary/10 hover:text-foreground"
				>
					<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
						/>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
						/>
					</svg>
					<span class="font-medium">Settings</span>
				</a>
			</nav>

			<div class="border-t border-border p-4">
				<div class="flex items-center gap-3 px-4 py-3">
					<div
						class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-primary to-primary-light font-bold text-white"
					>
						W
					</div>
					<div>
						<p class="text-sm font-semibold">Whisper Spring</p>
						<p class="text-xs text-secondary">v1.0.0</p>
					</div>
				</div>
			</div>
		</aside>

		<div class="flex flex-1 flex-col">
			<header class="sticky top-0 z-30 border-b border-border bg-background/80 backdrop-blur-lg">
				<div class="flex h-16 items-center justify-between px-6">
					<button
						onclick={toggleSidebar}
						class="rounded-lg p-2 transition-colors hover:bg-secondary/10 lg:hidden"
						aria-label="Toggle sidebar"
					>
						<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path
								stroke-linecap="round"
								stroke-linejoin="round"
								stroke-width="2"
								d="M4 6h16M4 12h16M4 18h16"
							/>
						</svg>
					</button>

					<div class="flex items-center gap-4">
						<div
							class="hidden items-center gap-2 rounded-xl border border-secondary/20 bg-secondary/10 px-4 py-2 sm:flex"
						>
							<svg
								class="h-5 w-5 text-secondary"
								fill="none"
								stroke="currentColor"
								viewBox="0 0 24 24"
							>
								<path
									stroke-linecap="round"
									stroke-linejoin="round"
									stroke-width="2"
									d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
								/>
							</svg>
							<input
								type="text"
								placeholder="Search..."
								class="w-48 border-none bg-transparent text-sm placeholder-secondary/50 outline-none"
							/>
						</div>
					</div>

					<div class="flex items-center gap-2">
						<button
							class="rounded-lg p-2 transition-colors hover:bg-secondary/10"
							aria-label="Notifications"
						>
							<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path
									stroke-linecap="round"
									stroke-linejoin="round"
									stroke-width="2"
									d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
								/>
							</svg>
						</button>
						<button
							class="rounded-lg p-2 transition-colors hover:bg-secondary/10"
							aria-label="Help"
						>
							<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
								<path
									stroke-linecap="round"
									stroke-linejoin="round"
									stroke-width="2"
									d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
								/>
							</svg>
						</button>
						<button
							onclick={toggleDarkMode}
							class="rounded-lg p-2 transition-colors hover:bg-secondary/10"
							aria-label="Toggle dark mode"
						>
							{#if isDark}
								<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"
									/>
								</svg>
							{:else}
								<svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
									/>
								</svg>
							{/if}
						</button>
					</div>
				</div>
			</header>

			<main class="flex-1 overflow-auto p-6">
				{@render children()}
			</main>

			<footer class="border-t border-border px-6 py-4">
				<p class="text-center text-sm text-secondary">
					© 2026 Whisper Spring. Built with SvelteKit & Tailwind CSS.
				</p>
			</footer>
		</div>
	</div>
</div>
