<script lang="ts">
  import { onDestroy } from 'svelte';
  import Button from '#lib/ui/Button.svelte';
  import Icon from '#lib/ui/Icon.svelte';

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

<div class="page live-page">
  <h2>Live</h2>

  <div class="card stage-card">
    <button
      onclick={toggleRecording}
      disabled={isConnecting}
      class="record"
      class:recording={isRecording}
      aria-label={isRecording ? 'Stop recording' : 'Start recording'}
    >
      {#if isConnecting}
        <Icon icon="pending" size="lg" />
      {:else if isRecording}
        <Icon icon="stop" size="lg" />
      {:else}
        <Icon icon="mic" size="lg" />
      {/if}
    </button>

    <p class="status">
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
      class="transcript scrollbar-hide"
      class:empty-state={!transcript && !error}
    >
      {#if transcript}
        <p>{transcript}</p>
      {:else if error}
        <p class="error-text">{error}</p>
      {:else}
        <p class="placeholder">Transcribed text will appear here...</p>
      {/if}
    </div>

    {#if transcript}
      <div class="copy-row">
        <Button
          onclick={copyTranscript}
          icon="content_copy"
          variant="text"
          tone="secondary"
          aria-label="Copy transcript"
        />
      </div>
    {/if}
  </div>
</div>

<style>
  /* The `.page` primitive only handles the page column; this page centres it */
  .live-page {
    align-items: center;
  }

  /* `.card` supplies the surface, shadow and radius; this adds the layout and
     the larger radius this page uses. */
  .stage-card {
    display: flex;
    width: 100%;
    max-width: var(--width-2xl);
    flex: 1;
    flex-direction: column;
    align-items: center;
    margin-top: var(--space-4);
    padding: var(--space-8);
    border-radius: var(--radius-xl);
  }

  .record {
    display: flex;
    align-items: center;
    justify-content: center;
    width: var(--size-hero);
    height: var(--size-hero);
    color: var(--color-on-primary);
    background-color: var(--color-primary);
    border-radius: var(--radius-full);
    cursor: pointer;
    transition:
      transform var(--duration-short),
      background-color var(--duration-short);
  }

  .record:not(.recording):hover:not(:disabled) {
    transform: scale(1.05);
    background-color: var(--color-primary-hover);
  }

  .record:disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  .record.recording {
    color: var(--color-on-error);
    background-color: var(--color-error);
    box-shadow: var(--shadow-error);
    transform: scale(1.1);
    animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
  }

  .status {
    margin-top: var(--space-4);
    color: var(--color-secondary);
  }

  .transcript {
    flex: 1;
    width: 100%;
    margin-top: var(--space-6);
    padding: var(--space-4);
    overflow-y: auto;
    background-color: var(--color-background);
    border-radius: var(--radius-lg);
    white-space: pre-wrap;
  }

  .transcript.empty-state {
    border: 1px dashed var(--color-border);
  }

  .error-text {
    color: var(--color-error);
  }

  .placeholder {
    color: var(--color-secondary);
    text-align: center;
  }

  .copy-row {
    display: flex;
    width: 100%;
    justify-content: flex-end;
    margin-top: var(--space-4);
  }
</style>
