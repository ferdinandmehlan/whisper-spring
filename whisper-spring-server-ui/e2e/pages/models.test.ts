import { expect, test } from '../fixtures';

test('models page loads', async ({ page }) => {
	await page.goto('/models');
	await expect(page.locator('h2:has-text("Models")')).toBeVisible();
});
