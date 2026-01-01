import path from 'path';
import { _electron } from 'playwright'
import os from 'os'
import { page, setApplication, setWindow } from '@/index';
import fs from 'fs';
import linq from 'linq';
import { electron } from '@/index';

export async function openProgram(): Promise<void> {
    const electronOfThis = await _electron.launch({
        args: [],
        executablePath: await getExecutablePath(),
        cwd: path.join(__dirname, "../../..", "electron"),
        locale: "en-US",
    });
    const window = await electronOfThis.firstWindow();
    setApplication(electronOfThis);
    setWindow(window);
    const CPUUsageText = await page.Home.CurrentRandomNumber()
    expect(await CPUUsageText.isVisible()).toBeTruthy()
}

export async function closeProgram(): Promise<void> {
    await electron.application.close();
}

async function getExecutablePath() {
    let executablePath = path.join(__dirname, "../../..", "electron", "output");
    executablePath = linq.from((await fs.promises.readdir(executablePath)))
        .where(s => s.includes("-unpacked"))
        .select(s => path.join(executablePath, s))
        .select(s => {
            if (os.platform() === "win32") {
                return path.join(s, "my-app.exe");
            } else {
                return path.join(s, "my-app");
            }
        })
        .single();
    return executablePath;
}