import * as ElectronRemote from '@electron/remote';
import * as NodeOsUtils from 'node-os-utils';
import * as fs from 'fs';
import * as IsLoadedUtil from '@/../main/util/IsLoadedUtil';
import * as IsNotShowForTestUtil from '@/../main/util/IsNotShowForTestUtil';
import * as StorageUtil from '@/../main/util/StorageUtil';
import { timer } from 'rxjs';

const electronRemote = window.require("@electron/remote") as typeof ElectronRemote;
const { isNotShowForTest } = (electronRemote.require("./util/IsNotShowForTestUtil") as typeof IsNotShowForTestUtil);
const { getIsLoaded, setIsLoadedToTrue } = (electronRemote.require("./util/IsLoadedUtil") as typeof IsLoadedUtil);

function enterFullScreen() {
    if (!isNotShowForTest) {
        electronRemote.getCurrentWindow().setMenuBarVisibility(false)
        electronRemote.getCurrentWindow().setFullScreen(true)
    }
}

function exitFullScreen() {
    electronRemote.getCurrentWindow().setFullScreen(false)
    electronRemote.getCurrentWindow().setMenuBarVisibility(true)
}

async function waitApplicationLoaded() {
    const isLoaded = getIsLoaded();
    if (!isLoaded) {
        const display = electronRemote.screen.getDisplayNearestPoint(electronRemote.screen.getCursorScreenPoint());
        const defaultWidth = display.workArea.width / 2;
        const defaultHeight = display.workArea.height / 2;
        const width = display.workArea.width > defaultWidth ? defaultWidth : display.workArea.width;
        const height = display.workArea.height > defaultHeight ? defaultHeight : display.workArea.height;
        electronRemote.getCurrentWindow().setBounds({
            width: Math.floor(width),
            height: Math.floor(height),
            x: Math.floor(display.workArea.x + ((display.workArea.width - width) / 2)),
            y: Math.floor(display.workArea.y + ((display.workArea.height - height) / 2)),
        });
        if (!isNotShowForTest) {
            electronRemote.getCurrentWindow().maximize();
            electronRemote.getCurrentWindow().show();
            electronRemote.getCurrentWindow().setAlwaysOnTop(true, "status");
            electronRemote.getCurrentWindow().focus();
            electronRemote.getCurrentWindow().moveTop();
        }
        electronRemote.getCurrentWindow().setAlwaysOnTop(false);
        electronRemote.getCurrentWindow().setMenuBarVisibility(true);
        electronRemote.getCurrentWindow().setTitle("React App");
        await timer(1).toPromise();
        setIsLoadedToTrue();
    }
}

function exitApp(){
    electronRemote.app.exit();
}

export default {
    fs: electronRemote.require('fs') as typeof fs,
    ElectronStorage: electronRemote.require("./util/StorageUtil") as typeof StorageUtil,
    NodeOsUtils: electronRemote.require("node-os-utils") as typeof NodeOsUtils,
    enterFullScreen,
    exitFullScreen,
    waitApplicationLoaded,
    exitApp,
}