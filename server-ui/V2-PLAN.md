# v2 — three-pane islands UI

Status: **mock only**. Nothing is wired to the API yet, nothing replaces v1 yet.
Live at `/v2`, side by side with the existing app.

```sh
./gradlew :server-ui:dev      # http://localhost:5173/v2
```

The mock is state-only: the rail switches the centre pane and the inspector,
the theme toggle flips the palette. There is no routing, no fetching, no
persistence — that is deliberate, so the layout can be judged before any of it
is built for real.

---

## 1. The idea

Three pages (transcriptions, live, settings, plus models) currently swap the
entire content area. That is fine for a file-based tool but it loses context:
the options panel lives in a modal sidebar, recent runs are invisible, and the
settings page and the per-run options are two separate worlds with the same
vocabulary.

v2 merges them into one shell:

```
┌──────────┐ ┌────────────────────────────────┐ ┌────────────┐
│  rail    │ │  work area                     │ │  inspector │
│          │ │                                │ │            │
│ nav      │ │  the current view:             │ │  context   │
│ recent   │ │  transcribe / live / models /  │ │  for the   │
│ runs     │ │  settings                      │ │  view      │
│          │ │                                │ │            │
│ status   │ │  crumb · actions · status bar  │ │            │
└──────────┘ └────────────────────────────────┘ └────────────┘
```

- **Left — navigation.** Stable across all views. Where am I, what did I run
  recently, is the server up.
- **Centre — work.** The only pane that changes with the view. It owns the
  header (breadcrumb + view actions) and the status bar.
- **Right — context.** Options for what is in the centre. Replaces
  `OptionsSidebar.svelte` and the (empty) settings page at once.

The rule that keeps the shell honest: **no view may add its own chrome**. If a
view needs a control, it goes in the centre header or the inspector. Views are
content, not pages.

## 2. Theme

Reference: IntelliJ "islands" (three floating rounded panes on a soft backdrop)

- Grok (monochrome, sharp contrast).

Two layers with opposite characters:

| Layer      | Character                                              |
| ---------- | ------------------------------------------------------ |
| Background | rough, glassy, out of focus — light pools + grain      |
| Panes      | flat, opaque, hard-edged — radius, hairline, no shadow |

The contrast is the whole effect. If a pane becomes translucent, or the
background becomes a clean gradient, the islands stop reading as islands.

Rules:

- **No hue.** Hierarchy comes from opacity steps (100 / 62 / 34 %), not colour.
  Red is reserved for failure (`--v2-danger`), nothing else gets to be coloured.
- **Hairlines, not shadows.** Depth in the panes is 1px at 8 % white. Shadows
  make panes float above the glass, which fights the flat-pane idea.
- **Type does the work.** 13px base, tight tracking (`-0.01em`), mono for
  anything numeric or machine-ish, small-caps eyebrows at 10px / 0.12em.
- **Inversion is the accent.** A primary action is white-on-black (or the
  reverse in light mode) — no accent colour to maintain.

Tokens live in `src/lib/v2/theme.css` and are scoped to `.v2`, so v1 keeps its
teal/slate theme while both ship.

### Modes

Both modes are designed, not inverted. The invariant: **the pane is always the
brighter, flatter surface; the stage always carries the blur.** What flips is
which side of the ramp the pane sits on.

|                | Dark                  | Light                                  |
| -------------- | --------------------- | -------------------------------------- |
| stage          | `#000`                | `#e7e7e7`                              |
| pane           | `#0a0a0a`             | `#fff`                                 |
| hairline       | white 8%              | black 10%                              |
| fill / on-fill | white / black         | black / white                          |
| stage glow     | white pools 26/14/10% | white glare 72% + black vignette 7/11% |
| grain          | `overlay` @0.30       | `multiply` @0.16                       |
| `--v2-chevron` | white 45%             | black 45%                              |

Mode is applied twice, on purpose:

- `:root[data-v2-mode]` — set by the inline script in `app.html` **before first
  paint** from `localStorage['v2-mode']`, falling back to
  `prefers-color-scheme`. `theme.css` resolves it down to `.v2`, so a light user
  never sees a dark flash while the SPA hydrates.
- `.v2[data-mode]` — the live Svelte state, written by the rail's segmented
  switch, which also updates the `<html>` attribute and the storage key.

v2 deliberately keeps its own key: `theme` belongs to v1's slate/teal palette,
`v2-mode` to the islands palette, so switching one never repaints the other.

Measured contrast on the pane surface:

| token                                            | dark   | light  |
| ------------------------------------------------ | ------ | ------ |
| `--v2-text` (body)                               | 19.8:1 | 19.8:1 |
| `--v2-text-2` (secondary body)                   | 7.8:1  | 6.6:1  |
| `--v2-text-3` (eyebrows, timestamps, status bar) | 3.0:1  | 3.3:1  |

`text-3` is deliberately below AA: it is used for 10px labels that are
decoration first. Any move to use it for real content must bump the alpha
(≈55% dark / ≈58% light for 4.5:1) and re-check the hierarchy.

## 3. Files in the mock

```
src/lib/v2/
  theme.css              tokens + stage + pane + shared primitives
  Shell.svelte           background layer, 3-pane grid, mode switch
  NavRail.svelte         left pane: brand, nav, recent, server status
  WorkArea.svelte        centre chrome: crumb, actions, scroll body, status bar
  Icon.svelte            stroke icon set (replaces Material Symbols in v2)
  mock/
    TranscribePane.svelte
    LivePane.svelte
    ModelsPane.svelte
    SettingsPane.svelte
    Inspector.svelte     right pane, switches on the active view
src/routes/v2/
  +layout.svelte         imports the theme
  +page.svelte           view state; composes the three panes
```

`src/routes/+layout.svelte` renders v2 without the v1 sidebar/topbar. That
branch is temporary and disappears when v2 replaces v1.

## 4. Plan

### Phase 0 — decide (blocks everything)

1. **Which pane owns what?** The mock assumes right = options, left = nav.
   The alternative is IntelliJ-style movable tool windows (left/right/bottom
   tabs). Decide before building: movable panes are a different component.
2. **Routing model.** The mock switches views in memory. Real v2 needs
   `/v2/transcribe`, `/v2/live`, … (deep-linkable, back button works) while the
   shell stays mounted. That means a nested layout per view, not local state.
3. **What happens to `/models`?** The mock keeps it as a view with the inspector
   showing details. It could equally be a section inside settings.
4. **Cutover.** Does `/` become v2 at some point, or does `/v2` become the only
   UI and v1 gets deleted? Plan for a flag, not a fork.

### Phase 1 — shell for real

- Move `view` state to routes: `v2/+layout.svelte` renders `Shell`, each view is
  a `+page.svelte` supplying centre + inspector content.
- Extract the repeated pane header/status-bar chrome so views cannot drift.
- Make panes resizable (drag handle on the gaps, `localStorage` persistence,
  keyboard-resizable for a11y). Min widths from the mock: 200 / 480 / 260.
- Persist and install theme mode: `prefers-color-scheme` default, explicit
  override, no flash on load (same trick as v1's `app.html` script, scoped to
  `data-mode`).
- Responsive ladder as in the mock: <1180px narrower panes, <1024px right pane
  hidden behind a toggle, <820px left pane becomes an icon rail.

### Phase 2 — move the features over

| v1                                            | v2                                                                              |
| --------------------------------------------- | ------------------------------------------------------------------------------- |
| `routes/transcriptions/+page.svelte`          | centre view `transcribe` + inspector options                                    |
| `routes/transcriptions/OptionsSidebar.svelte` | `Inspector.svelte` for `transcribe` (sections, not one long scroll)             |
| `routes/live/+page.svelte`                    | centre view `live`, recorder in the centre, device/level in the inspector       |
| `routes/settings/+page.svelte` (placeholder)  | `settings` view; per-run options move to the inspector, only defaults stay here |
| `routes/models/+page.svelte` (placeholder)    | `models` view; inspector shows active model + storage                           |

Shared behaviour that must not be duplicated: `lib/transcriptions/*` (settings
load/save, request mapping, streaming client). Keep them as-is and let both
shells use them until v1 is deleted.

### Phase 3 — polish & correctness

- Keyboard: `1/2/3` view switching, `,` settings, `Cmd+K` recent-runs palette,
  `Esc` closes inspector overlays. Shortcut hints are already in the mock's rail.
- Empty/loading/error states for each view — the mock only shows the happy path,
  which is the easiest thing to forget.
- Focus handling when views swap (focus the centre heading, announce the change).
- Contrast check: 34 % white on #0a0a0a is ~3.4:1 — fine for the eyebrows and
  status bar, **not** for body text. Audit before shipping.
- `prefers-reduced-motion` is already respected globally in `theme.css`.

### Phase 4 — retheme v1's shared components (only if v1 survives)

`Button`, `Input`, `Select`, `Toggle`, `Slider` currently render from v1 tokens.
Either port them into `v2/ui/` with the new tokens, or keep them as they are and
accept two visual languages during the transition. Do not "tweak" the shared
components — v1 would drop to a half-theme.

## 5. Open questions

- Do we want the recent-runs list to be client-side history only, or should the
  server grow an endpoint for past transcriptions? The mock assumes history.
- Live view: does the inspector need per-session options, or are they server
  config that only settings may touch?
- Is a fourth view needed for "logs"? The status bar suggests a place for it,
  and a bottom panel would fit the islands language.
