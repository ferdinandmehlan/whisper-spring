import { expect, test } from '../fixtures';

test('v2 shell renders three panes', async ({ page }) => {
  await page.goto('/v2');

  await expect(page.locator('.v2-pane')).toHaveCount(3);
  await expect(page.locator('.v2-pane--main .current')).toHaveText('transcribe');
});

test('v2 rail switches the centre pane and its inspector', async ({ page }) => {
  await page.goto('/v2');

  await page.locator('.item', { hasText: 'Live' }).click();

  await expect(page.locator('.v2-pane--main .current')).toHaveText('live');
  await expect(page.locator('.v2-pane--main .live-chip')).toBeVisible();
  await expect(page.locator('.v2-pane--right .v2-eyebrow').first()).toHaveText('input');
});

test('v2 keeps a single shell when navigating views', async ({ page }) => {
  await page.goto('/v2');
  const shell = page.locator('.v2-panes');

  await page.locator('.item', { hasText: 'Settings' }).click();

  await expect(shell).toBeVisible();
  await expect(page.locator('.v2-pane--main .current')).toHaveText('settings');
});

test('v2 light theme swaps the palette and survives a reload', async ({ page }) => {
  await page.goto('/v2');

  await page.getByRole('button', { name: 'Light theme' }).click();

  await expect(page.locator('.v2')).toHaveAttribute('data-mode', 'light');
  // The pre-paint attribute is what prevents a dark flash; it must be kept in sync.
  await expect(page.locator('html')).toHaveAttribute('data-v2-mode', 'light');

  // Light mode is a designed theme, not an inversion: panes are white on a
  // grey stage, hairlines go black instead of white.
  expect(
    await page
      .locator('.v2-pane')
      .first()
      .evaluate((el) => getComputedStyle(el).backgroundColor)
  ).toBe('rgb(255, 255, 255)');
  expect(
    await page
      .locator('.v2-pane')
      .first()
      .evaluate((el) => getComputedStyle(el).borderTopColor)
  ).toBe('rgba(0, 0, 0, 0.1)');

  await page.reload();

  await expect(page.locator('.v2')).toHaveAttribute('data-mode', 'light');
  await expect(page.getByRole('button', { name: 'Light theme' })).toHaveAttribute(
    'aria-pressed',
    'true'
  );
});
