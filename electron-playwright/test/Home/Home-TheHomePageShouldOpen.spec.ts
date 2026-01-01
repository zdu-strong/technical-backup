import { page, action } from '@/index';


test('', async () => {
    const CurrentCPUUsage = await page.Home.CurrentRandomNumber();
    expect(await CurrentCPUUsage.isVisible()).toBeTruthy()
})

beforeEach(async () => {
    await action.openProgram();
})

afterEach(async () => {
    await action.closeProgram();
})