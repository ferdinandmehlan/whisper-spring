<script lang="ts">
  import Button from '#/lib/components/Button.svelte';
  import Input from '#/lib/components/Input.svelte';
  import Select from '#/lib/components/Select.svelte';
  import Slider from '#/lib/components/Slider.svelte';
  import Switch from '#/lib/components/Switch.svelte';
  import { options } from '#/lib/transcriptions/options.svelte';

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

  const BEST_OF = [1, 2, 3, 4, 5, 6, 7, 8];
  const BEAMS = [-1, 1, 2, 3, 4, 5];

  function toggle(key: 'translate' | 'splitOnWord') {
    options.update({ [key]: !options.settings[key] });
  }
</script>

<section class="app-block" role="group" aria-labelledby="options-general">
  <h3 class="app-eyebrow" id="options-general">general</h3>

  <div class="app-field">
    <span class="app-sub">language</span>
    <Select
      aria-label="Language"
      bind:value={options.settings.language}
      onchange={() => options.persist()}
    >
      {#each LANGUAGES as language (language.value)}
        <option value={language.value}>{language.label}</option>
      {/each}
    </Select>
  </div>

  <div class="app-toggle-row">
    <span>Translate to English</span>
    <Switch
      pressed={options.settings.translate}
      label="Translate to English"
      ontoggle={() => toggle('translate')}
    />
  </div>
</section>

<section class="app-block" role="group" aria-labelledby="options-decoding">
  <h3 class="app-eyebrow" id="options-decoding">decoding</h3>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">temperature</span>
      <span class="app-mono app-value">{options.settings.temperature.toFixed(1)}</span>
    </div>
    <Slider
      min="0"
      max="1"
      step="0.1"
      aria-label="Temperature"
      bind:value={options.settings.temperature}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">temperature increment</span>
      <span class="app-mono app-value">{options.settings.temperatureInc.toFixed(1)}</span>
    </div>
    <Slider
      min="0"
      max="1"
      step="0.1"
      aria-label="Temperature increment"
      bind:value={options.settings.temperatureInc}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">max segment length</span>
      <span class="app-mono app-value"
        >{options.settings.maxLen === 0 ? 'unlimited' : `${options.settings.maxLen} chars`}</span
      >
    </div>
    <Slider
      min="0"
      max="100"
      step="5"
      aria-label="Max segment length"
      bind:value={options.settings.maxLen}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-toggle-row">
    <span>Split on word boundary</span>
    <Switch
      pressed={options.settings.splitOnWord}
      label="Split on word boundary"
      ontoggle={() => toggle('splitOnWord')}
    />
  </div>

  <div class="app-field">
    <span class="app-sub">best candidates</span>
    <Select
      aria-label="Best candidates"
      bind:value={options.settings.bestOf}
      onchange={() => options.persist()}
    >
      {#each BEST_OF as n (n)}
        <option value={n}>{n}</option>
      {/each}
    </Select>
  </div>

  <div class="app-field">
    <span class="app-sub">beam size</span>
    <Select
      aria-label="Beam size"
      bind:value={options.settings.beamSize}
      onchange={() => options.persist()}
    >
      {#each BEAMS as n (n)}
        <option value={n}>{n === -1 ? '-1 (greedy)' : n}</option>
      {/each}
    </Select>
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">max context tokens</span>
      <!-- -1 is the sentinel, so the readout says what it means. -->
      <span class="app-mono app-value"
        >{options.settings.maxContext === -1 ? 'unlimited' : options.settings.maxContext}</span
      >
    </div>
    <Input
      type="number"
      min="-1"
      aria-label="Max context tokens"
      bind:value={options.settings.maxContext}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <span class="app-sub">audio context</span>
    <Input
      type="number"
      min="0"
      aria-label="Audio context"
      bind:value={options.settings.audioContext}
      onchange={() => options.persist()}
    />
  </div>
</section>

<section class="app-block" role="group" aria-labelledby="options-thresholds">
  <h3 class="app-eyebrow" id="options-thresholds">thresholds</h3>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">entropy</span>
      <span class="app-mono app-value">{options.settings.entropyThreshold.toFixed(1)}</span>
    </div>
    <Slider
      min="0"
      max="5"
      step="0.1"
      aria-label="Entropy threshold"
      bind:value={options.settings.entropyThreshold}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">log probability</span>
      <span class="app-mono app-value">{options.settings.logprobThreshold.toFixed(1)}</span>
    </div>
    <Slider
      min="-5"
      max="0"
      step="0.1"
      aria-label="Log probability threshold"
      bind:value={options.settings.logprobThreshold}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">word</span>
      <span class="app-mono app-value">{options.settings.wordThreshold.toFixed(2)}</span>
    </div>
    <Slider
      min="0"
      max="1"
      step="0.01"
      aria-label="Word threshold"
      bind:value={options.settings.wordThreshold}
      onchange={() => options.persist()}
    />
  </div>
</section>

<section class="app-block" role="group" aria-labelledby="options-offsets">
  <h3 class="app-eyebrow" id="options-offsets">offsets</h3>

  <div class="app-field">
    <span class="app-sub">time (ms)</span>
    <Input
      type="number"
      min="0"
      aria-label="Time offset in milliseconds"
      bind:value={options.settings.offsetTMs}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <span class="app-sub">segment index</span>
    <Input
      type="number"
      min="0"
      aria-label="Segment index offset"
      bind:value={options.settings.offsetN}
      onchange={() => options.persist()}
    />
  </div>

  <div class="app-field">
    <div class="app-field-head">
      <span class="app-sub">duration (ms)</span>
      <!-- 0 means "to the end", same as `unlimited` above. -->
      <span class="app-mono app-value"
        >{options.settings.durationMs === 0 ? 'all' : options.settings.durationMs}</span
      >
    </div>
    <Input
      type="number"
      min="0"
      aria-label="Duration in milliseconds"
      bind:value={options.settings.durationMs}
      onchange={() => options.persist()}
    />
  </div>
</section>

<Button onclick={() => options.reset()}>Restore defaults</Button>
