<script lang="ts">
	import { resolve } from '$app/paths';
</script>

<div class="flex min-h-[60vh] flex-col items-center justify-center space-y-4 text-center">
	<h1 class="text-6xl font-bold">404</h1>
	<p class="text-xl text-secondary">Page not found</p>
	<p class="text-sm text-secondary">
		The page you are looking for doesn't exist or has been moved.
	</p>
	<a
		href={resolve('/')}
		class="text-primary-foreground mt-4 rounded-lg bg-primary px-6 py-2 font-medium transition-colors hover:bg-primary/90"
	>
		Go home
	</a>
</div>
