<script lang="ts">
  type IconSize = 'sm' | 'md' | 'lg';
  type IconTone = 'default' | 'primary' | 'secondary' | 'error';

  type IconProps = {
    icon: string;
    size?: IconSize;
    tone?: IconTone;
    bounce?: boolean;
    dim?: boolean;
  };

  let { icon, size = 'md', tone = 'default', bounce = false, dim = false }: IconProps = $props();
</script>

<!--
  Icons are decorative: the ligature name ("mic", "settings", ...) is not a
  label. Interactive elements that rely on an icon must carry aria-label.
-->
<span
  class="icon material-symbols-outlined"
  class:sm={size === 'sm'}
  class:md={size === 'md'}
  class:lg={size === 'lg'}
  class:primary={tone === 'primary'}
  class:secondary={tone === 'secondary'}
  class:error={tone === 'error'}
  class:bounce
  class:dim
  aria-hidden="true">{icon}</span
>

<style>
  .icon {
    font-size: var(--icon-md);
    line-height: 1;
  }

  .sm {
    font-size: var(--icon-sm);
  }

  .md {
    font-size: var(--icon-md);
  }

  /* Same box as the old `text-4xl` icons: 36px glyph on a 40px line. */
  .lg {
    font-size: var(--icon-lg);
    line-height: var(--leading-4xl);
  }

  .primary {
    color: var(--color-primary-text);
  }

  .secondary {
    color: var(--color-secondary);
  }

  .error {
    color: var(--color-error);
  }

  .bounce {
    animation: bounce 1s infinite;
  }

  .dim {
    opacity: 0.5;
  }
</style>
