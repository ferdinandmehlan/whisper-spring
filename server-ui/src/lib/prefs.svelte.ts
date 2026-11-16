/**
 * App preferences: theme mode and pane collapse.
 *
 * They live together because both are user-visible state that outlives a view
 * and is read from more than one place - the appearance section sets the theme,
 * the layout hydrates it, and the collapse keys are shared by two panes.
 * A module store (rather than the layout's script) is what lets the collapse
 * buttons sit inside the rail and the inspector without threading callbacks.
 */
export type Mode = 'dark' | 'light';

class Prefs {
  mode = $state<Mode>('dark');
  leftCollapsed = $state(false);
  rightCollapsed = $state(false);

  /**
   * Syncs with the mode `app.html` resolved before first paint. The attribute
   * on <html> is the single source of truth - the pre-paint script, this
   * store and the stylesheet all agree on it - so no second copy sits on the
   * shell root where it could fall out of sync.
   */
  hydrate() {
    const initial = document.documentElement.dataset.theme;
    if (initial === 'light' || initial === 'dark') {
      this.mode = initial;
    }
  }

  setMode(next: Mode) {
    this.mode = next;
    document.documentElement.dataset.theme = next;
    try {
      localStorage.setItem('theme', next);
    } catch {
      // Persisting is best effort; the attribute above already applied it.
    }
  }

  toggleLeft() {
    this.leftCollapsed = !this.leftCollapsed;
  }

  toggleRight() {
    this.rightCollapsed = !this.rightCollapsed;
  }

  /** IntelliJ's "hide all tool windows": both, or neither back. */
  toggleAll() {
    const hide = !(this.leftCollapsed && this.rightCollapsed);
    this.leftCollapsed = hide;
    this.rightCollapsed = hide;
  }
}

export const prefs = new Prefs();
