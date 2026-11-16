export interface ModelInfo {
  id: string;
  name: string;
  sizeBytes: number | null;
}

interface ModelListResponse {
  active: string | null;
  models: ModelInfo[];
}

async function fetchModelList(): Promise<ModelListResponse> {
  const response = await fetch('/api/models');
  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`);
  }
  return (await response.json()) as ModelListResponse;
}

type Status = 'idle' | 'loading' | 'ready' | 'error';

class ModelsState {
  status = $state<Status>('idle');
  error = $state<string | null>(null);
  active = $state<string | null>(null);
  list = $state<ModelInfo[]>([]);

  get loaded(): ModelInfo | undefined {
    return this.list.find((model) => model.id === this.active);
  }

  get totalBytes(): number {
    return this.list.reduce((sum, model) => sum + (model.sizeBytes ?? 0), 0);
  }

  async load() {
    if (this.status === 'loading' || this.status === 'ready') return;

    this.status = 'loading';
    this.error = null;
    try {
      const data = await fetchModelList();
      this.active = data.active;
      this.list = data.models ?? [];
      this.status = 'ready';
    } catch (e) {
      this.status = 'error';
      this.error = e instanceof Error ? e.message : 'could not reach the server';
    }
  }
}

export const models = new ModelsState();
