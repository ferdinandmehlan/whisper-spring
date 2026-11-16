<script lang="ts">
  interface Props {
    pressed: boolean;
    label: string;
    ontoggle: () => void;
  }

  let { pressed, label, ontoggle }: Props = $props();
</script>

<button class="switch" aria-pressed={pressed} aria-label={label} onclick={ontoggle}>
  <span></span>
</button>

<style>
  .switch {
    position: relative;
    width: 32px;
    height: 18px;
    flex: none;
    padding: 0;
    background: transparent;
    border: 1px solid var(--app-control-line);
    border-radius: 99px;
    cursor: pointer;
  }

  .switch span {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 12px;
    height: 12px;
    background: var(--app-text-3);
    border-radius: 50%;
    transition: transform var(--app-dur) var(--app-ease);
  }

  .switch[aria-pressed='true'] {
    background: var(--app-fill);
    border-color: var(--app-fill);
  }

  .switch[aria-pressed='true'] span {
    background: var(--app-on-fill);
    transform: translateX(14px);
  }
</style>
