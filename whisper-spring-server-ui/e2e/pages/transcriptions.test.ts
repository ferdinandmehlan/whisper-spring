import { expect, test } from '../fixtures';

test('transcriptions page loads', async ({ page }) => {
  await page.goto('/transcriptions');
  await expect(
    page.locator('p:has-text("Drop or upload an audio file to get started")')
  ).toBeVisible();
});
