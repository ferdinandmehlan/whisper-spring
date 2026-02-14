import { expect, test } from '../fixtures';

test('live page loads', async ({ page }) => {
	await page.goto('/live');
	await expect(page.locator('h2:has-text("Live")')).toBeVisible();
});
