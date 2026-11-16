export const WAVE_BARS = 68;
export const SAMPLE_RATE = 16000;
export const BLOCK_SIZE = 4096;

class LiveState {
  isRecording = $state(false);
  isConnecting = $state(false);
  messages = $state<string[]>([]);
  level = $state(0);
  levels = $state<number[]>([]);
  error = $state<string | null>(null);
  segments = $state(0);
  elapsedMs = $state(0);

  get transcript(): string {
    return this.messages.join(' ');
  }

  get phase(): 'connecting' | 'recording' | 'idle' {
    if (this.isConnecting) return 'connecting';
    if (this.isRecording) return 'recording';
    return 'idle';
  }

  #ws: WebSocket | null = null;
  #audioCtx: AudioContext | null = null;
  #stream: MediaStream | null = null;
  #source: MediaStreamAudioSourceNode | null = null;
  #processor: ScriptProcessorNode | null = null;
  #gain: GainNode | null = null;
  #startedAt = 0;
  #ticker: ReturnType<typeof setInterval> | null = null;

  async toggle() {
    if (this.isRecording || this.isConnecting) {
      this.stop();
    } else {
      await this.start();
    }
  }

  async start() {
    this.error = null;
    this.messages = [];
    this.levels = [];
    this.segments = 0;
    this.elapsedMs = 0;
    this.level = 0;

    try {
      this.#stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.#audioCtx = new AudioContext({ sampleRate: SAMPLE_RATE });

      this.isConnecting = true;
      this.#ws = new WebSocket(liveUrl());

      this.#ws.onopen = () => {
        this.isConnecting = false;
        this.isRecording = true;
        this.#startedAt = Date.now();
        this.#ticker = setInterval(() => {
          this.elapsedMs = Date.now() - this.#startedAt;
        }, 100);

        const ctx = this.#audioCtx;
        const stream = this.#stream;
        if (!ctx || !stream) return;

        this.#source = ctx.createMediaStreamSource(stream);
        this.#processor = ctx.createScriptProcessor(BLOCK_SIZE, 1, 1);

        this.#processor.onaudioprocess = (event: AudioProcessingEvent) => {
          if (this.#ws?.readyState !== WebSocket.OPEN) return;

          const input = event.inputBuffer.getChannelData(0);
          const pcm = new Int16Array(input.length);
          let peak = 0;
          for (let i = 0; i < input.length; i++) {
            const sample = Math.max(-1, Math.min(1, input[i]));
            peak = Math.max(peak, Math.abs(sample));
            pcm[i] = sample < 0 ? sample * 0x8000 : sample * 0x7fff;
          }
          // Smooth the meter: a raw peak per 256 ms block flickers.
          this.level = Math.max(peak, this.level * 0.7);
          this.#pushLevel(this.level);
          this.#ws.send(pcm.buffer);
        };

        this.#gain = ctx.createGain();
        this.#gain.gain.value = 0;

        this.#source.connect(this.#processor);
        this.#processor.connect(this.#gain);
        this.#gain.connect(ctx.destination);
      };

      this.#ws.onmessage = (event: MessageEvent) => {
        this.messages = [...this.messages, String(event.data)];
        this.segments += 1;
      };

      this.#ws.onerror = () => {
        if (this.isConnecting) {
          this.error = 'Could not connect to transcription server';
        } else {
          this.error = 'WebSocket connection error';
        }
        this.stop();
      };

      this.#ws.onclose = () => {
        if (this.isRecording) {
          this.error = 'Connection closed';
        }
        this.stop();
      };
    } catch (e) {
      this.isConnecting = false;
      if (e instanceof Error) {
        if (e.name === 'NotAllowedError' || e.name === 'PermissionDeniedError') {
          this.error = 'Microphone access denied. Please allow microphone permissions.';
        } else if (e.name === 'NotSupportedError') {
          this.error = 'Audio format not supported by your browser.';
        } else {
          this.error = e.message;
        }
      } else {
        this.error = 'An unexpected error occurred';
      }
      this.cleanup();
    }
  }

  stop() {
    this.isConnecting = false;
    this.isRecording = false;
    this.cleanup();
  }

  clear() {
    this.messages = [];
    this.segments = 0;
    this.elapsedMs = 0;
    this.error = null;
  }

  #pushLevel(value: number) {
    const next = this.levels.length >= WAVE_BARS ? this.levels.slice(1) : this.levels.slice();
    next.push(value);
    this.levels = next;
  }

  cleanup() {
    this.level = 0;

    if (this.#ticker) {
      clearInterval(this.#ticker);
      this.#ticker = null;
    }
    if (this.#processor) {
      this.#processor.disconnect();
      this.#processor = null;
    }
    if (this.#gain) {
      this.#gain.disconnect();
      this.#gain = null;
    }
    if (this.#source) {
      this.#source.disconnect();
      this.#source = null;
    }
    if (this.#audioCtx) {
      this.#audioCtx.close().catch(() => {});
      this.#audioCtx = null;
    }
    if (this.#ws) {
      const socket = this.#ws;
      this.#ws = null;
      socket.onopen = null;
      socket.onclose = null;
      socket.onerror = null;
      socket.onmessage = null;
      if (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING) {
        socket.close();
      }
    }
    if (this.#stream) {
      this.#stream.getTracks().forEach((track) => track.stop());
      this.#stream = null;
    }
  }
}

function liveUrl(): string {
  const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${proto}//${window.location.host}/api/live`;
}

export const live = new LiveState();
