import { test as base, Page, Response } from '@playwright/test';

export { expect } from '@playwright/test';

export const test = base.extend({
	// We redefine the 'page' fixture
	page: async ({ page }, use) => {
		const originalGoto = page.goto.bind(page);

		// Patch the goto method directly on the existing page object
		page.goto = async (
			url: string,
			options?: Parameters<Page['goto']>[1]
		): Promise<Response | null> => {
			const response = await originalGoto(url, {
				waitUntil: 'domcontentloaded',
				...options
			});

			// Wait for external fonts loaded
			await page.evaluate(() => document.fonts.ready);
			return response;
		};

		await use(page);
	}
});
