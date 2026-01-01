import { timer } from 'rxjs';
import { page, action } from '@/index';

test('', async () => {
    const GameRenderer = await page.Game.GameRenderer();
    expect(await GameRenderer.isVisible()).toBeTruthy();
    await timer(2000).toPromise();
})

beforeEach(async () => {
    await action.openProgram();
    const EnterTheGame = await page.Home.EnterTheGame();
    await EnterTheGame.click()
})

afterEach(async () => {
    await action.closeProgram();
})