<script lang="ts">
  import { onDestroy } from 'svelte';
  import Button from '$lib/ui/Button.svelte';
  import Icon from '$lib/ui/Icon.svelte';

  let isRecording = $state(false);
  let isConnecting = $state(false);
  let transcript = $state('');
  let error = $state<string | null>(null);

  let ws: WebSocket | null = null;
  let audioCtx: AudioContext | null = null;
  let stream: MediaStream | null = null;
  let sourceNode: MediaStreamAudioSourceNode | null = null;
  let scriptNode: ScriptProcessorNode | null = null;
  let gainNode: GainNode | null = null;
  let transcriptEl: HTMLDivElement | undefined;

  function wsUrl(): string {
    const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${proto}//${window.location.host}/api/live`;
  }

  async function toggleRecording() {
    if (isRecording || isConnecting) {
      stopRecording();
    } else {
      await startRecording();
    }
  }

  async function startRecording() {
    error = null;
    transcript = '';

    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      audioCtx = new AudioContext({ sampleRate: 16000 });

      isConnecting = true;
      ws = new WebSocket(wsUrl());

      ws.onopen = () => {
        isConnecting = false;
        isRecording = true;

        sourceNode = audioCtx!.createMediaStreamSource(stream!);
        scriptNode = audioCtx!.createScriptProcessor(4096, 1, 1);

        scriptNode.onaudioprocess = (event: AudioProcessingEvent) => {
          if (ws?.readyState !== WebSocket.OPEN) return;
          const input = event.inputBuffer.getChannelData(0);
          const pcm = new Int16Array(input.length);
          for (let i = 0; i < input.length; i++) {
            const s = Math.max(-1, Math.min(1, input[i]));
            pcm[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
          }
          ws.send(pcm.buffer);
        };

        gainNode = audioCtx!.createGain();
        gainNode.gain.value = 0;

        sourceNode.connect(scriptNode);
        scriptNode.connect(gainNode);
        gainNode.connect(audioCtx!.destination);
      };

      ws.onmessage = (event: MessageEvent) => {
        transcript += event.data + ' ';
      };

      ws.onerror = () => {
        if (isConnecting) {
          error = 'Could not connect to transcription server';
          isConnecting = false;
          cleanup();
        } else {
          error = 'WebSocket connection error';
          stopRecording();
        }
      };

      ws.onclose = () => {
        if (isRecording) {
          error = 'Connection closed';
          stopRecording();
        }
        isConnecting = false;
      };
    } catch (e) {
      isConnecting = false;
      if (e instanceof Error) {
        if (e.name === 'NotAllowedError' || e.name === 'PermissionDeniedError') {
          error = 'Microphone access denied. Please allow microphone permissions.';
        } else if (e.name === 'NotSupportedError') {
          error = 'Audio format not supported by your browser.';
        } else {
          error = e.message;
        }
      } else {
        error = 'An unexpected error occurred';
      }
      cleanup();
    }
  }

  function stopRecording() {
    isConnecting = false;
    cleanup();
  }

  function cleanup() {
    isRecording = false;

    if (scriptNode) {
      scriptNode.disconnect();
      scriptNode = null;
    }
    if (gainNode) {
      gainNode.disconnect();
      gainNode = null;
    }
    if (sourceNode) {
      sourceNode.disconnect();
      sourceNode = null;
    }
    if (audioCtx) {
      audioCtx.close().catch(() => {});
      audioCtx = null;
    }
    if (ws) {
      ws.onopen = null;
      ws.onclose = null;
      ws.onerror = null;
      ws.onmessage = null;
      if (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING) {
        ws.close();
      }
      ws = null;
    }
    if (stream) {
      stream.getTracks().forEach((t) => t.stop());
      stream = null;
    }
  }

  function copyTranscript() {
    if (transcript) {
      navigator.clipboard.writeText(transcript.trim());
    }
  }

  $effect(() => {
    if (transcript && transcriptEl) {
      transcriptEl.scrollTop = transcriptEl.scrollHeight;
    }
  });

  onDestroy(cleanup);
</script>

<div class="flex min-h-0 flex-1 flex-col items-center p-6">
  <h2>Live</h2>

  <div
    class="mt-4 flex w-full max-w-2xl flex-1 flex-col items-center rounded-2xl bg-surface p-8 shadow-md"
  >
    <button
      onclick={toggleRecording}
      disabled={isConnecting}
      class="flex h-24 w-24 items-center justify-center rounded-full transition-all
        {isRecording
          ? 'scale-110 animate-pulse bg-error text-white shadow-lg shadow-error/50'
          : 'bg-primary text-foreground enabled:hover:scale-105 enabled:hover:bg-primary-hover'}
        disabled:cursor-not-allowed disabled:opacity-50"
      aria-label={isRecording ? 'Stop recording' : 'Start recording'}
    >
      {#if isConnecting}
        <Icon icon="pending" class="text-4xl!" />
      {:else if isRecording}
        <Icon icon="stop" class="text-4xl!" />
      {:else}
        <Icon icon="mic" class="text-4xl!" />
      {/if}
    </button>

    <p class="mt-4 text-secondary">
      {#if isConnecting}
        Connecting...
      {:else if isRecording}
        Recording...
      {:else if transcript}
        Tap to start again
      {:else}
        Tap to start
      {/if}
    </p>

    <div
      bind:this={transcriptEl}
      class="scrollbar-hide mt-6 w-full flex-1 overflow-y-auto whitespace-pre-wrap rounded-xl bg-background p-4"
      class:border={!transcript && !error}
      class:border-dashed={!transcript && !error}
      class:border-border={!transcript && !error}
    >
      {#if transcript}
        <p>{transcript}</p>
      {:else if error}
        <p class="text-error">{error}</p>
      {:else}
        <p class="text-center text-secondary">Transcribed text will appear here...</p>
      {/if}
    </div>

    {#if transcript}
      <div class="mt-4 flex w-full justify-end">
        <Button onclick={copyTranscript} icon="content_copy" variant="text" aria-label="Copy transcript" class="text-secondary" />
      </div>
    {/if}
  </div>
</div>
