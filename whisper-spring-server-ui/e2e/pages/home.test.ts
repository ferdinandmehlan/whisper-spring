import { expect, test } from '../fixtures';

test('home page loads', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('h1')).toBeVisible();
});
