<script lang="ts">
  import Button from '$lib/ui/Button.svelte';
  import Input from '$lib/ui/Input.svelte';
  import Sidebar from '$lib/ui/Sidebar.svelte';
  import Select from '$lib/ui/Select.svelte';
  import Slider from '$lib/ui/Slider.svelte';
  import Toggle from '$lib/ui/Toggle.svelte';
  import {
    loadSettings,
    saveSettings,
    resetSettings,
    type TranscriptionSettings
  } from '$lib/transcriptions';

  let { showSettings, onClose }: { showSettings: boolean; onClose: () => void } = $props();

  let settings = $state<TranscriptionSettings>(loadSettings());

  function persistSettings() {
    saveSettings(settings);
  }

  const LANGUAGES: { value: string; label: string }[] = [
    { value: 'auto', label: 'Auto-detect' },
    { value: 'en', label: 'English' },
    { value: 'de', label: 'German' },
    { value: 'fr', label: 'French' },
    { value: 'es', label: 'Spanish' },
    { value: 'it', label: 'Italian' },
    { value: 'pt', label: 'Portuguese' },
    { value: 'nl', label: 'Dutch' },
    { value: 'ja', label: 'Japanese' },
    { value: 'zh', label: 'Chinese' },
    { value: 'ko', label: 'Korean' },
    { value: 'ru', label: 'Russian' },
    { value: 'ar', label: 'Arabic' },
    { value: 'hi', label: 'Hindi' },
    { value: 'tr', label: 'Turkish' },
    { value: 'pl', label: 'Polish' },
    { value: 'sv', label: 'Swedish' },
    { value: 'da', label: 'Danish' },
    { value: 'fi', label: 'Finnish' },
    { value: 'no', label: 'Norwegian' }
  ];
</script>

<Sidebar open={showSettings} {onClose} title="Settings">
  <div class="flex flex-col gap-4">
    <Select
      id="language"
      label="Language"
      bind:value={settings.language}
      options={LANGUAGES}
      onchange={persistSettings}
    />

    <Toggle bind:checked={settings.translate} label="Translate" onchange={persistSettings} />

    <Toggle
      bind:checked={settings.includeTimestamps}
      label="Include timestamps"
      onchange={persistSettings}
    />

    <hr class="border-border" />

    <Slider
      id="maxLen"
      label="Max segment length"
      bind:value={settings.maxLen}
      min={0}
      max={100}
      step={5}
      formatValue={(v) => (v === 0 ? 'unlimited' : `${v} chars`)}
      oninput={persistSettings}
    />

    <Toggle
      bind:checked={settings.splitOnWord}
      label="Split on word boundary"
      onchange={persistSettings}
    />

    <Select
      id="bestOf"
      label="Best candidates"
      bind:value={settings.bestOf}
      options={[
        { value: 1, label: '1' },
        { value: 2, label: '2' },
        { value: 3, label: '3' },
        { value: 4, label: '4' },
        { value: 5, label: '5' },
        { value: 6, label: '6' },
        { value: 7, label: '7' },
        { value: 8, label: '8' }
      ]}
      onchange={persistSettings}
    />

    <hr class="border-border" />

    <Slider
      id="temperature"
      label="Temperature"
      bind:value={settings.temperature}
      min={0}
      max={1}
      step={0.1}
      formatValue={(v) => `(${v.toFixed(1)})`}
      oninput={persistSettings}
    />

    <Slider
      id="temperatureInc"
      label="Temperature increment"
      bind:value={settings.temperatureInc}
      min={0}
      max={1}
      step={0.1}
      formatValue={(v) => v.toFixed(1)}
      oninput={persistSettings}
    />

    <Select
      id="beamSize"
      label="Beam size"
      bind:value={settings.beamSize}
      options={[
        { value: -1, label: '-1 (greedy)' },
        { value: 1, label: '1' },
        { value: 2, label: '2' },
        { value: 3, label: '3' },
        { value: 4, label: '4' },
        { value: 5, label: '5' }
      ]}
      onchange={persistSettings}
    />

    <hr class="border-border" />

    <Input
      id="offsetTMs"
      label="Time offset (ms)"
      bind:value={settings.offsetTMs}
      type="number"
      min={0}
      onchange={persistSettings}
    />

    <Input
      id="offsetN"
      label="Segment index offset"
      bind:value={settings.offsetN}
      type="number"
      min={0}
      onchange={persistSettings}
    />

    <Input
      id="durationMs"
      label="Duration (ms)"
      bind:value={settings.durationMs}
      type="number"
      min={0}
      formatValue={(v) => (v === 0 ? 'all' : `${v} ms`)}
      onchange={persistSettings}
    />

    <hr class="border-border" />

    <Input
      id="maxContext"
      label="Max context tokens"
      bind:value={settings.maxContext}
      type="number"
      min={-1}
      formatValue={(v) => (v === -1 ? 'unlimited' : `${v}`)}
      onchange={persistSettings}
    />

    <Input
      id="audioContext"
      label="Audio context"
      bind:value={settings.audioContext}
      type="number"
      min={0}
      formatValue={(v) => (v === 0 ? 'all' : `${v}`)}
      onchange={persistSettings}
    />

    <hr class="border-border" />

    <Slider
      id="wordThreshold"
      label="Word threshold"
      bind:value={settings.wordThreshold}
      min={0}
      max={1}
      step={0.01}
      formatValue={(v) => `(${v.toFixed(2)})`}
      oninput={persistSettings}
    />

    <Slider
      id="entropyThreshold"
      label="Entropy threshold"
      bind:value={settings.entropyThreshold}
      min={0}
      max={5}
      step={0.1}
      formatValue={(v) => `(${v.toFixed(1)})`}
      oninput={persistSettings}
    />

    <Slider
      id="logprobThreshold"
      label="Log probability threshold"
      bind:value={settings.logprobThreshold}
      min={-5}
      max={0}
      step={0.1}
      formatValue={(v) => `(${v.toFixed(1)})`}
      oninput={persistSettings}
    />

    <Button
      onclick={resetSettings}
      aria-label="Reset settings"
      icon="restart_alt"
      variant="outlined"
    >
      Reset
    </Button>
  </div>
</Sidebar>
