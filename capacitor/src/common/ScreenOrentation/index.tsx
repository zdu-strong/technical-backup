import { StatusBar } from '@capacitor/status-bar'
import { ScreenOrientation } from '@awesome-cordova-plugins/screen-orientation'
import { Capacitor } from '@capacitor/core'
import { AndroidFullScreen } from '@awesome-cordova-plugins/android-full-screen'
import { Subject, concatMap, from, retry } from 'rxjs'
import { EdgeToEdge } from "@capawesome/capacitor-android-edge-to-edge-support";

const subject = new Subject<string>();

subject.pipe(
    concatMap((type) => from((async () => {
        if (type === ScreenOrientation.ORIENTATIONS.LANDSCAPE) {
            if (Capacitor.getPlatform() === "web") {
                return;
            }

            await ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.LANDSCAPE);

            if (Capacitor.getPlatform() === "android") {
                await AndroidFullScreen.immersiveMode()
            }

            await StatusBar.hide();
            await StatusBar.setOverlaysWebView({ overlay: true });
            await EdgeToEdge.disable();
        } else if (type === ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY) {
            if (Capacitor.getPlatform() === "web") {
                return;
            }

            await ScreenOrientation.lock(ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY);
            await StatusBar.setOverlaysWebView({ overlay: false });
            await EdgeToEdge.enable();

            if (Capacitor.getPlatform() === "android") {
                await AndroidFullScreen.showSystemUI()
            }

            await StatusBar.show();
        }
    })())),
    retry(),
).subscribe();

export function LANDSCAPE() {
    subject.next(ScreenOrientation.ORIENTATIONS.LANDSCAPE);
}

export function PORTRAIT_PRIMARY() {
    subject.next(ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY);
}

if (ScreenOrientation.type !== ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY) {
    subject.next(ScreenOrientation.ORIENTATIONS.PORTRAIT_PRIMARY);
}