import { getContext, onDestroy, setContext, type Snippet } from 'svelte';

const INSPECTOR = Symbol('inspector');

export interface InspectorContent {
  title: string;
  content: Snippet;
}

class InspectorSlot {
  current = $state.raw<InspectorContent | undefined>();
  private owner: object | undefined;

  register(title: string, content: Snippet) {
    const owner = {};
    this.owner = owner;
    this.current = { title, content };

    return () => {
      if (this.owner === owner) {
        this.owner = undefined;
        this.current = undefined;
      }
    };
  }
}

export function provideInspector() {
  return setContext(INSPECTOR, new InspectorSlot());
}

export function useInspector(title: string, content: Snippet) {
  const slot = getContext<InspectorSlot | undefined>(INSPECTOR);
  if (!slot) throw new Error('Inspector content must be registered within the app layout');
  const unregister = slot.register(title, content);
  onDestroy(unregister);
}
