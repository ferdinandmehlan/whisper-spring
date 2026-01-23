import { expect, test } from '../fixtures';

test('settings page loads', async ({ page }) => {
	await page.goto('/settings');
	await expect(page.locator('h2:has-text("Settings")')).toBeVisible();
});
