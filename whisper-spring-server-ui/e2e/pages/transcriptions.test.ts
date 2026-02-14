import { expect, test } from '../fixtures';

test('transcriptions page loads', async ({ page }) => {
	await page.goto('/transcriptions');
	await expect(page.locator('h2:has-text("Transcriptions")')).toBeVisible();
});
