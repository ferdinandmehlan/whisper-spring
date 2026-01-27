<script lang="ts">
	export let aiInput = '';
</script>

<div class="flex h-screen bg-background text-foreground">
	<!-- Left Sidebar Navigation -->
	<div class="flex w-16 flex-col items-center space-y-4 border-r border-border bg-card py-4">
		<button class="rounded-lg p-3 transition-colors hover:bg-muted" aria-label="Menu">
			<svg class="h-5 w-5 text-foreground" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M4 6h16M4 12h16M4 18h16"
				/>
			</svg>
		</button>
		<button class="rounded-lg p-3 transition-colors hover:bg-muted" aria-label="Edit">
			<svg class="h-5 w-5 text-foreground" fill="none" stroke="currentColor" viewBox="0 0 24 24">
				<path
					stroke-linecap="round"
					stroke-linejoin="round"
					stroke-width="2"
					d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
				/>
			</svg>
		</button>
		<button class="rounded-lg p-3 transition-colors hover:bg-muted" aria-label="Settings">
			<svg class="h-5 w-5 text-foreground" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
		</button>
	</div>

	<!-- Main Content Area -->
	<div class="flex flex-1 flex-col">
		<!-- Global Header -->
		<header class="flex items-center justify-between border-b border-border bg-card px-6 py-4">
			<div class="flex-1 text-center">
				<h1 class="flex items-center justify-center gap-2 text-xl font-bold text-foreground">
					Connect Llama.cpp Vision to OpenCode
					<svg
						class="h-4 w-4 text-muted-foreground"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M19 9l-7 7-7-7"
						/>
					</svg>
				</h1>
			</div>
			<div class="flex items-center gap-3">
				<button
					class="rounded-lg px-3 py-1.5 text-sm font-medium text-white transition-colors"
					style="background-color: var(--contrast-primary)"
				>
					Upgrade to Google AI Plus
				</button>
				<div
					class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground"
				>
					F
				</div>
			</div>
		</header>

		<!-- Main Content -->
		<main class="flex-1 overflow-y-auto p-6">
			<div class="mx-auto max-w-4xl space-y-8">
				<!-- Section 1: Configure the Vision Subagent -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">1. Configure the "Vision Subagent"</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>Set up the vision subagent to process images and extract visual information.</p>
						<p>This involves configuring the model parameters and input processing pipeline.</p>
					</div>

					<!-- Code Block -->
					<div
						class="relative rounded-lg border border-border"
						style="background-color: var(--contrast-bg-code)"
					>
						<div class="flex items-center justify-between border-b border-border px-4 py-2">
							<span class="text-sm font-medium text-foreground">Configuration</span>
							<button class="rounded p-1 hover:bg-muted" aria-label="Copy code">
								<svg
									class="h-4 w-4 text-muted-foreground"
									fill="none"
									stroke="currentColor"
									viewBox="0 0 24 24"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
									/>
								</svg>
							</button>
						</div>
						<pre class="p-4 text-sm"><code
								class="language-json"
								style="color: var(--color-text-main)"
								>{`{
				  "model": "llama-vision",
				  "parameters": {
				    "temperature": 0.7,
				    "max_tokens": 2048,
				    "vision_resolution": "high"
				  },
				  "preprocessing": {
				    "resize": true,
				    "normalize": true
				  }
				}`}</code
							></pre>
					</div>
				</section>

				<!-- Section 2: Automatic Handover Workflow -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">2. The Automatic Handover Workflow</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>
							Implement the workflow that seamlessly transitions between vision and text processing.
						</p>
						<p>
							The system automatically detects when to hand over from vision to text-based agents.
						</p>
					</div>

					<!-- Code Block -->
					<div
						class="relative rounded-lg border border-border"
						style="background-color: var(--contrast-bg-code)"
					>
						<div class="flex items-center justify-between border-b border-border px-4 py-2">
							<span class="text-sm font-medium text-foreground">Workflow Handler</span>
							<button class="rounded p-1 hover:bg-muted" aria-label="Copy code">
								<svg
									class="h-4 w-4 text-muted-foreground"
									fill="none"
									stroke="currentColor"
									viewBox="0 0 24 24"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
									/>
								</svg>
							</button>
						</div>
						<pre class="p-4 text-sm"><code
								class="language-python"
								style="color: var(--color-text-main)"
								>{`def handover_workflow(input_data):
				  if has_visual_content(input_data):
				    result = vision_agent.process(input_data)
				    if needs_text_processing(result):
				      return text_agent.process(result)
				  return default_agent.process(input_data)`}</code
							></pre>
					</div>
				</section>

				<!-- Section 3: Advanced Configuration Options -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">3. Advanced Configuration Options</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>Explore advanced settings for fine-tuning your vision subagent.</p>
						<p>These options allow for more precise control over model behavior and performance.</p>
					</div>

					<!-- Code Block -->
					<div
						class="relative rounded-lg border border-border"
						style="background-color: var(--contrast-bg-code)"
					>
						<div class="flex items-center justify-between border-b border-border px-4 py-2">
							<span class="text-sm font-medium text-foreground">Advanced Settings</span>
							<button class="rounded p-1 hover:bg-muted" aria-label="Copy code">
								<svg
									class="h-4 w-4 text-muted-foreground"
									fill="none"
									stroke="currentColor"
									viewBox="0 0 24 24"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
									/>
								</svg>
							</button>
						</div>
						<pre class="p-4 text-sm"><code
								class="language-json"
								style="color: var(--color-text-main)"
								>{`{
				  "advanced": {
				    "beam_search": true,
				    "top_k": 50,
				    "top_p": 0.95,
				    "repeat_penalty": 1.2,
				    "presence_penalty": 0.1,
				    "frequency_penalty": 0.2
				  },
				  "optimization": {
				    "batch_size": 8,
				    "memory_efficient": true,
				    "quantization": "int8"
				  }
				}`}</code
							></pre>
					</div>
				</section>

				<!-- Section 4: Error Handling and Recovery -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">4. Error Handling and Recovery</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>Implement robust error handling mechanisms for your workflow.</p>
						<p>This ensures graceful degradation when issues occur during processing.</p>
					</div>

					<!-- Code Block -->
					<div
						class="relative rounded-lg border border-border"
						style="background-color: var(--contrast-bg-code)"
					>
						<div class="flex items-center justify-between border-b border-border px-4 py-2">
							<span class="text-sm font-medium text-foreground">Error Handling</span>
							<button class="rounded p-1 hover:bg-muted" aria-label="Copy code">
								<svg
									class="h-4 w-4 text-muted-foreground"
									fill="none"
									stroke="currentColor"
									viewBox="0 0 24 24"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
									/>
								</svg>
							</button>
						</div>
						<pre class="p-4 text-sm"><code
								class="language-python"
								style="color: var(--color-text-main)"
								>{`def safe_handover_workflow(input_data):
				  try:
				    if has_visual_content(input_data):
				      result = vision_agent.process(input_data)
				      if needs_text_processing(result):
				        return text_agent.process(result)
				    return default_agent.process(input_data)
				  except VisionProcessingError as e:
				    logger.error(f"Vision processing failed: {e}")
				    return fallback_agent.process(input_data)
				  except Exception as e:
				    logger.error(f"Unexpected error: {e}")
				    return default_agent.process(input_data)`}</code
							></pre>
					</div>
				</section>

				<!-- Section 5: Performance Monitoring -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">5. Performance Monitoring</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>Set up monitoring to track the performance of your workflow.</p>
						<p>This helps identify bottlenecks and optimize resource usage.</p>
					</div>

					<!-- Code Block -->
					<div
						class="relative rounded-lg border border-border"
						style="background-color: var(--contrast-bg-code)"
					>
						<div class="flex items-center justify-between border-b border-border px-4 py-2">
							<span class="text-sm font-medium text-foreground">Monitoring Setup</span>
							<button class="rounded p-1 hover:bg-muted" aria-label="Copy code">
								<svg
									class="h-4 w-4 text-muted-foreground"
									fill="none"
									stroke="currentColor"
									viewBox="0 0 24 24"
								>
									<path
										stroke-linecap="round"
										stroke-linejoin="round"
										stroke-width="2"
										d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
									/>
								</svg>
							</button>
						</div>
						<pre class="p-4 text-sm"><code
								class="language-javascript"
								style="color: var(--color-text-main)"
								>{`const monitor = new PerformanceMonitor({
				  trackMetrics: ['processing_time', 'memory_usage', 'cpu_load'],
				  alertThresholds: {
				    processing_time: { warning: 5000, critical: 10000 },
				    memory_usage: { warning: 0.8, critical: 0.95 }
				  },
				  reporting: {
				    interval: '1m',
				    destinations: ['console', 'prometheus', 'datadog']
				  }
				});`}</code
							></pre>
					</div>
				</section>

				<!-- Section 6: Example Chat History -->
				<section class="space-y-4">
					<h2 class="text-lg font-semibold text-foreground">6. Example Chat History</h2>
					<div class="space-y-2 text-muted-foreground">
						<p>Here are some examples of previous conversations and their outcomes.</p>
						<p>These demonstrate the workflow in action with real-world scenarios.</p>
					</div>

					<!-- Chat Example 1 -->
					<div class="rounded-lg border border-border p-4">
						<div class="flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground"
							>
								U
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>User:</strong> Can you analyze this image of a complex mechanical assembly and
									explain how it works?
								</p>
							</div>
						</div>
						<div class="mt-3 flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-medium"
							>
								A
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>Assistant:</strong> This appears to be a gearbox assembly with multiple interlocking
									components. The vision system has identified 12 distinct parts including gears, bearings,
									and housing. Would you like me to provide a detailed breakdown of each component and
									their interactions?
								</p>
							</div>
						</div>
					</div>

					<!-- Chat Example 2 -->
					<div class="rounded-lg border border-border p-4">
						<div class="flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground"
							>
								U
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>User:</strong> What's the best approach to optimize this workflow for processing
									large batches of images?
								</p>
							</div>
						</div>
						<div class="mt-3 flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-medium"
							>
								A
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>Assistant:</strong> For batch processing optimization, I recommend:
								</p>
								<ul class="mt-2 list-disc pl-5 text-sm text-muted-foreground">
									<li>Implement parallel processing with worker pools</li>
									<li>Use memory-efficient data loading with generators</li>
									<li>Configure appropriate batch sizes based on your GPU memory</li>
									<li>Implement caching for repeated operations</li>
									<li>Consider using a distributed processing framework for very large datasets</li>
								</ul>
							</div>
						</div>
					</div>

					<!-- Chat Example 3 -->
					<div class="rounded-lg border border-border p-4">
						<div class="flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground"
							>
								U
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>User:</strong> How do I handle cases where the vision system can't confidently
									identify objects in an image?
								</p>
							</div>
						</div>
						<div class="mt-3 flex items-start gap-3">
							<div
								class="flex h-8 w-8 items-center justify-center rounded-full bg-muted text-sm font-medium"
							>
								A
							</div>
							<div class="flex-1">
								<p class="text-sm text-foreground">
									<strong>Assistant:</strong> For low-confidence scenarios, you can implement several
									strategies:
								</p>
								<ol class="mt-2 list-decimal pl-5 text-sm text-muted-foreground">
									<li>Set confidence thresholds and fall back to human review</li>
									<li>Use ensemble methods with multiple models</li>
									<li>Implement active learning to improve the model over time</li>
									<li>Provide users with tools to manually annotate ambiguous cases</li>
									<li>Use context from surrounding conversations to make better guesses</li>
								</ol>
							</div>
						</div>
					</div>
				</section>
			</div>
		</main>

		<!-- Floating Chat Input Container - moved inside content column with same width as chat messages -->
		<div class="sticky bottom-0 z-[1000] mt-auto">
			<div class="mx-auto max-w-4xl px-6">
				<div
					class="flex items-center gap-2 border-t border-gray-300 bg-white/95 px-4 py-3 shadow-[0_4px_12px_rgba(0,0,0,0.05)] backdrop-blur-md"
				>
					<input
						type="text"
						bind:value={aiInput}
						placeholder="Ask anything..."
						class="flex-1 rounded-lg border-none bg-gray-50 px-3 py-2 transition-colors outline-none hover:bg-gray-100 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none"
					/>
					<button
						class="flex h-8 w-8 cursor-pointer items-center justify-center rounded-full border-none bg-gray-200 transition-colors hover:bg-gray-300"
						aria-label="Send message"
					>
						<svg
							class="h-4 w-4 text-gray-700"
							fill="none"
							stroke="currentColor"
							viewBox="0 0 24 24"
						>
							<path
								stroke-linecap="round"
								stroke-linejoin="round"
								stroke-width="2"
								d="M5 10l7-7m0 0l7 7m-7-7v18"
							/>
						</svg>
					</button>
					<span class="rounded bg-gray-100 px-2 py-1 text-xs whitespace-nowrap text-gray-600">
						gemma-3-4b-it-Q4_K_M.gguf
					</span>
				</div>
			</div>
		</div>
	</div>
</div>
