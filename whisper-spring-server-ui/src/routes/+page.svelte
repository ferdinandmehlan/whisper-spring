<script lang="ts">
  import { onMount } from 'svelte';

  interface Message {
    id: string;
    type: 'user' | 'assistant';
    content: string;
    timestamp: Date;
  }

  let messages: Message[] = [];
  let selectedFile: File | null = null;
  let isUploading = false;
  let dragOver = false;
  let fileInput: HTMLInputElement;

  function handleFileSelect(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    if (file) {
      selectedFile = file;
    }
  }

  function handleDragOver(event: DragEvent) {
    event.preventDefault();
    dragOver = true;
  }

  function handleDragLeave(event: DragEvent) {
    event.preventDefault();
    dragOver = false;
  }

  function handleDrop(event: DragEvent) {
    event.preventDefault();
    dragOver = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      selectedFile = files[0];
    }
  }

  async function sendAudio() {
    if (!selectedFile) return;

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      type: 'user',
      content: `Uploaded audio file: ${selectedFile.name}`,
      timestamp: new Date()
    };
    messages = [...messages, userMessage];

    isUploading = true;

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);

      const response = await fetch('http://localhost:8080/inference', {
        method: 'POST',
        body: formData
      });

      if (response.ok) {
        const result = await response.text();

        const assistantMessage: Message = {
          id: (Date.now() + 1).toString(),
          type: 'assistant',
          content: result,
          timestamp: new Date()
        };
        messages = [...messages, assistantMessage];
      } else {
        throw new Error('Transcription failed');
      }
    } catch (error) {
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        type: 'assistant',
        content: `Error: ${error instanceof Error ? error.message : 'Unknown error occurred'}`,
        timestamp: new Date()
      };
      messages = [...messages, errorMessage];
    } finally {
      isUploading = false;
      selectedFile = null;
      if (fileInput) {
        fileInput.value = '';
      }
    }
  }

  function clearFile() {
    selectedFile = null;
    if (fileInput) {
      fileInput.value = '';
    }
  }
</script>

<div class="chat-container flex flex-col h-screen">
  <!-- Header -->
  <div class="bg-gray-800 p-4 border-b border-gray-600">
    <h1 class="text-xl font-semibold text-white">Whisper Chat</h1>
    <p class="text-gray-400 text-sm">Upload audio files to get transcriptions</p>
  </div>

  <!-- Messages -->
  <div class="flex-1 overflow-y-auto p-4 space-y-4">
    {#each messages as message}
      <div class="message-bubble {message.type}" class:user={message.type === 'user'} class:assistant={message.type === 'assistant'}>
        <div class="text-sm opacity-75 mb-1">
          {message.type === 'user' ? 'You' : 'Whisper'} • {message.timestamp.toLocaleTimeString()}
        </div>
        <div class="whitespace-pre-wrap">{message.content}</div>
      </div>
    {/each}

    {#if isUploading}
      <div class="message-bubble assistant">
        <div class="text-sm opacity-75 mb-1">Whisper • {new Date().toLocaleTimeString()}</div>
        <div class="flex items-center space-x-2">
          <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
          <span>Transcribing audio...</span>
        </div>
      </div>
    {/if}
  </div>

  <!-- Input Area -->
  <div class="p-4 border-t border-gray-600">
    {#if selectedFile}
      <div class="mb-4 p-3 bg-gray-700 rounded-lg flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <span class="text-green-400">📁</span>
          <span class="text-white">{selectedFile.name}</span>
          <span class="text-gray-400">({(selectedFile.size / 1024 / 1024).toFixed(2)} MB)</span>
        </div>
        <button
          on:click={clearFile}
          class="text-red-400 hover:text-red-300"
          aria-label="Remove file"
        >
          ✕
        </button>
      </div>
    {/if}

    <div
      class="upload-area {dragOver ? 'dragover' : ''}"
      on:dragover={handleDragOver}
      on:dragleave={handleDragLeave}
      on:drop={handleDrop}
    >
      <input
        type="file"
        accept=".wav"
        on:change={handleFileSelect}
        class="hidden"
        id="file-input"
        bind:this={fileInput}
      />
      <label for="file-input" class="cursor-pointer">
        <div class="text-gray-400 mb-2">
          <span class="text-2xl">🎵</span>
        </div>
        <div class="text-white font-medium mb-1">
          {selectedFile ? 'Change audio file' : 'Click to upload or drag & drop'}
        </div>
        <div class="text-gray-500 text-sm">
          Supports mono, 16kHz, 16-bit WAV files
        </div>
      </label>
    </div>

    <button
      on:click={sendAudio}
      disabled={!selectedFile || isUploading}
      class="w-full mt-4 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white font-medium py-3 px-4 rounded-lg transition-colors"
    >
      {isUploading ? 'Transcribing...' : 'Send Audio'}
    </button>
  </div>
</div>
