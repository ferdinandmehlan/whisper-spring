<script lang="ts">
	import Icon from '$lib/ui/Icon.svelte';
	import Button from '$lib/ui/Button.svelte';
	import Sheet from '$lib/ui/Sheet.svelte';
	import Toggle from '$lib/ui/Toggle.svelte';

	interface Segment {
		start: number;
		end: number;
		text: string;
	}

	interface InferenceResponse {
		text: string;
		segments: Segment[];
	}

	let selectedFile = $state<File | null>(null);
	let fileInput = $state<HTMLInputElement | null>(null);
	let prompt = $state('');
	let stream = $state(false);
	let isLoading = $state(false);
	let error = $state<string | null>(null);
	let result = $state<InferenceResponse | null>(null);

	let liveText = $state('');
	let liveSegments = $state<Segment[]>([]);

	function handleFileSelect(event: Event) {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files[0]) {
			selectedFile = input.files[0];
			error = null;
		}
	}

	function handleDrop(event: DragEvent) {
		event.preventDefault();
		if (event.dataTransfer?.files && event.dataTransfer.files[0]) {
			selectedFile = event.dataTransfer.files[0];
			error = null;
		}
	}

	function clearFile() {
		selectedFile = null;
		if (fileInput) {
			fileInput.value = '';
		}
	}

	function handleDragOver(event: DragEvent) {
		event.preventDefault();
	}

	function formatTime(ms: number): string {
		const seconds = Math.floor(ms / 1000);
		const minutes = Math.floor(seconds / 60);
		const remainingSeconds = seconds % 60;
		return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
	}

	async function handleSubmit() {
		if (!selectedFile) {
			error = 'Please select an audio file';
			return;
		}

		isLoading = true;
		error = null;
		result = null;
		liveText = '';
		liveSegments = [];

		const formData = new FormData();
		formData.append('file', selectedFile);
		if (prompt.trim()) {
			formData.append('prompt', prompt.trim());
		}
		formData.append('stream', stream.toString());

		try {
			if (stream) {
				await handleStreamingSubmit(formData);
			} else {
				const response = await fetch('/api/transcription', {
					method: 'POST',
					body: formData
				});

				if (!response.ok) {
					const errorText = await response.text();
					throw new Error(errorText || `HTTP ${response.status}`);
				}

				result = await response.json();
			}
		} catch (err) {
			error = err instanceof Error ? err.message : 'Failed to transcribe audio';
		} finally {
			isLoading = false;
		}
	}

	async function handleStreamingSubmit(formData: FormData) {
		const response = await fetch('/api/transcription', {
			method: 'POST',
			body: formData
		});

		if (!response.ok) {
			const errorText = await response.text();
			throw new Error(errorText || `HTTP ${response.status}`);
		}

		const reader = response.body?.getReader();
		if (!reader) {
			throw new Error('Response body is not readable');
		}

		const decoder = new TextDecoder();

		while (true) {
			const { done, value } = await reader.read();
			if (done) break;

			const chunk = decoder.decode(value, { stream: true });
			const lines = chunk.split('\n');

			for (const line of lines) {
				if (line.startsWith('data:')) {
					const data = line.slice(5).trim();
					if (data && data !== '[DONE]') {
						try {
							const segment = JSON.parse(data) as Segment;
							liveSegments = [...liveSegments, segment];
							liveText = liveText + segment.text + ' ';
						} catch {
							// Ignore parse errors for incomplete chunks
						}
					}
				}
			}
		}

		result = {
			text: liveText.trim(),
			segments: liveSegments
		};
	}

	function getDisplayText(): string {
		if (stream && isLoading) {
			return liveText;
		}
		return result?.text || '';
	}

	function getDisplaySegments(): Segment[] {
		if (stream && isLoading) {
			return liveSegments;
		}
		return result?.segments || [];
	}
</script>

<div class="space-y-6">
	<div class="flex items-center justify-between">
		<h2 class="text-2xl font-bold tracking-tight">Transcriptions</h2>
	</div>

	<div class="grid gap-6 lg:grid-cols-2">
		<div class="space-y-6">
			<Sheet title="Upload Audio">
				<div
					class="relative rounded-lg border-2 border-dashed border-border p-8 text-center transition-colors hover:border-primary"
					ondrop={handleDrop}
					ondragover={handleDragOver}
					role="button"
					tabindex="0"
				>
					<div class="flex flex-col items-center gap-2">
						<Icon icon="upload" class="text-4xl! text-secondary" />
						<span class="font-medium">Drop audio file here</span>
						<span class="text-sm text-secondary">or click to browse</span>
					</div>
					<input
						type="file"
						accept="audio/*"
						class="absolute inset-0 cursor-pointer opacity-0"
						onchange={handleFileSelect}
						bind:this={fileInput}
					/>
				</div>

				{#if selectedFile}
					<div class="mt-4 flex items-center justify-between rounded-lg bg-background p-4">
						<div class="flex items-center gap-3">
							<Icon icon="audio_file" class="text-primary" />
							<div>
								<span class="font-medium">{selectedFile.name}</span>
								<span class="ml-2 text-sm text-secondary">
									{(selectedFile.size / 1024 / 1024).toFixed(2)} MB
								</span>
							</div>
						</div>
						<Button
							variant="text"
							icon="close"
							onclick={clearFile}
							aria-label="Remove file"
							class="text-error"
						/>
					</div>
				{/if}
			</Sheet>
			<Sheet title="Options">
				<div class="mb-4">
					<label for="prompt" class="mb-2 block text-sm font-medium">
						Initial Prompt
						<span class="text-secondary">(optional)</span>
					</label>
					<textarea
						id="prompt"
						bind:value={prompt}
						placeholder="Enter context to help with transcription (e.g., speaker names, technical terms)"
						class="w-full resize-none rounded-lg border-none bg-background px-4 py-3 text-sm focus:border-primary focus:ring-2 focus:ring-primary/20 focus:outline-none"
						rows="4"
					></textarea>
					<p class="mt text-xs text-secondary">Max 500 characters</p>
				</div>
				<div class="mb-6">
					<Toggle bind:checked={stream} label="Stream results via SSE" />
					<p class="mt-1 text-xs text-secondary">
						Receive transcription segments in real-time as they are generated
					</p>
				</div>
				<Button
					variant="filled"
					onclick={handleSubmit}
					disabled={!selectedFile || isLoading}
					class="w-full"
				>
					{#if isLoading}
						<Icon icon="sync" class="animate-spin [animation-duration:1.5s]" />
						Transcribing...
					{:else}
						<Icon icon="speech_to_text" />
						Start Transcription
					{/if}
				</Button>
			</Sheet>
		</div>

		<div class="space-y-6">
			{#if error}
				<div class="rounded-xl border border-error/50 bg-error/10 p-6">
					<div class="flex items-center gap-2 text-error">
						<Icon icon="error" class="h-5 w-5" />
						<span class="font-medium">Error</span>
					</div>
					<p class="mt-2 text-sm">{error}</p>
				</div>
			{:else if (stream && isLoading && (liveText || liveSegments.length > 0)) || result}
				<Sheet title="Result">
					<div class="space-y-4">
						<div class="rounded-lg bg-background p-4">
							<p class="whitespace-pre-wrap">{getDisplayText()}</p>
							{#if stream && isLoading}
								<span class="inline-block h-4 w-2 animate-pulse bg-current"></span>
							{/if}
						</div>
					</div>
				</Sheet>

				{#if getDisplaySegments().length > 0}
					<Sheet title="Segments">
						<div class="space-y-2">
							{#each getDisplaySegments() as segment, i (i)}
								<div class="flex gap-3 rounded-lg bg-background p-3">
									<span class="shrink-0 font-mono text-sm whitespace-nowrap text-secondary">
										{formatTime(segment.start)} → {formatTime(segment.end)}
									</span>
									<span class="whitespace-pre-wrap">{segment.text}</span>
								</div>
							{/each}
						</div>
					</Sheet>
				{:else if stream && isLoading}
					<Sheet title="Segments">
						<div class="flex items-center justify-center py-4 text-secondary">
							<Icon icon="sync" class="mr-2 animate-spin [animation-duration:1.5s]" />
							Waiting for segments...
						</div>
					</Sheet>
				{/if}
			{:else}
				<Sheet class="flex h-full min-h-[300px] items-center justify-center">
					<div class="text-center text-secondary">
						<Icon icon="audio_file" class="mx-auto text-4xl! opacity-50" />
						<p class="mt-4">Upload an audio file to get started</p>
					</div>
				</Sheet>
			{/if}
		</div>
	</div>
</div>
