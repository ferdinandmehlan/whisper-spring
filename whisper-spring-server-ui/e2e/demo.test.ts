import { expect, test } from '@playwright/test';

test('home page has expected h1', async ({ page }) => {
	// This test verifies that the home page contains an h1 element that is visible to the user.
	// It ensures the page is correctly rendered and the main heading is present.
	await page.goto('/');
	await expect(page.locator('h1')).toBeVisible();
});
