import { defineConfig } from '@playwright/test';

export default defineConfig({
	testDir: 'e2e',
	use: {
		// Base URL to use in actions like `await page.goto('/')`.
		baseURL: 'http://localhost:8080',
		// Collect trace when retrying the failed test.
		trace: 'on-first-retry'
	}
});
