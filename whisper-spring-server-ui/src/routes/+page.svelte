<script lang="ts">
	import ChatHistoryContainer from '$lib/ChatHistoryContainer.svelte';
	let aiInput = '';
</script>

<ChatHistoryContainer bind:aiInput />
