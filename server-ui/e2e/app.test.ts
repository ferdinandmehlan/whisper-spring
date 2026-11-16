import { expect, test } from '@playwright/test';

test('app boots into the workspace shell', async ({ page }) => {
  await page.goto('/');

  await expect(page).toHaveURL(/\/transcription$/);
  await expect(page.locator('.app-panes')).toHaveCount(1);
  await expect(page.locator('.app-pane')).toHaveCount(3);
});

test('navigation shortcuts work with the rail open and collapsed', async ({ page }) => {
  await page.goto('/transcription');
  await page.getByRole('button', { name: 'Collapse workspace' }).click();
  await page.keyboard.press('2');
  await expect(page).toHaveURL(/\/live$/);
  await page.keyboard.press('3');
  await expect(page).toHaveURL(/\/models$/);
  await page.getByRole('button', { name: 'Expand workspace' }).click();
  await expect(page.getByRole('link', { name: 'Models' })).toHaveAttribute('aria-current', 'page');
  await page.keyboard.press('0');
  await expect(page).toHaveURL(/\/settings$/);
  await expect(page.getByRole('link', { name: 'Settings' })).toHaveAttribute(
    'aria-current',
    'page'
  );
  await page.keyboard.press('1');
  await expect(page).toHaveURL(/\/transcription$/);
  const language = page.getByRole('combobox', { name: 'Language' });
  await language.focus();
  await page.keyboard.press('2');
  await expect(page).toHaveURL(/\/transcription$/);
});
