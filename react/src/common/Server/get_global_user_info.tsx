import { UserModel } from '@model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { from, fromEvent, retry, switchMap } from 'rxjs';
import { TypedJSON } from 'typedjson';
import { existsWindow } from '@common/exists-window/exists-window';
import { UserEmailModel } from '@model/UserEmailModel';

export const GlobalUserInfo = observable({
    id: '',
    username: '',
    accessToken: '',
    userEmailList: [] as UserEmailModel[],
    menuOpen: true,
} as UserModel);

export const GlobalMenuOpen = observable({
    menuOpen: false
});

export async function setGlobalUserInfo(user?: UserModel): Promise<void> {
    const hasParam = !!user;
    if (!hasParam) {
        const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
        if (jsonStringOfLocalStorage) {
            user = new TypedJSON(UserModel).parse(jsonStringOfLocalStorage)!;
        } else {
            removeGlobalUserInfo();
            return;
        }
    }

    GlobalUserInfo.id = user!.id;
    GlobalUserInfo.username = user!.username;
    GlobalUserInfo.accessToken = user!.accessToken;
    GlobalUserInfo.userEmailList = user!.userEmailList;
    if (typeof user!.menuOpen === "boolean") {
        GlobalUserInfo.menuOpen = user!.menuOpen;
    }
    if (hasParam) {
        window.localStorage.setItem(keyOfGlobalUserInfoOfLocalStorage, JSON.stringify(GlobalUserInfo));
    }
}

export function removeGlobalUserInfo() {
    GlobalUserInfo.id = '';
    GlobalUserInfo.username = '';
    GlobalUserInfo.accessToken = '';
    GlobalUserInfo.userEmailList = [] as UserEmailModel[];
    if (window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage)) {
        window.localStorage.removeItem(keyOfGlobalUserInfoOfLocalStorage);
    }
}

export function setGlobalMenuOpen(menuOpen?: boolean) {
    const hasParam = typeof menuOpen === "boolean";
    if (!hasParam) {
        const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalMenuOpenOfLocalStorage);
        if (jsonStringOfLocalStorage) {
            GlobalMenuOpen.menuOpen = !!new TypedJSON(UserModel).parse(jsonStringOfLocalStorage)!.menuOpen;
        } else {
            removeGlobalMenuOpen();
        }
        return;
    }

    GlobalMenuOpen.menuOpen = menuOpen;
    if (hasParam) {
        window.localStorage.setItem(keyOfGlobalMenuOpenOfLocalStorage, JSON.stringify(GlobalMenuOpen));
    }
}

export function removeGlobalMenuOpen() {
    GlobalMenuOpen.menuOpen = false;
    if (window.localStorage.getItem(keyOfGlobalMenuOpenOfLocalStorage)) {
        window.localStorage.removeItem(keyOfGlobalMenuOpenOfLocalStorage);
    }
}

const keyOfGlobalUserInfoOfLocalStorage = 'GlobalUserInfo-c12e6be9-e969-4a54-b5d4-b451755bf49a';
const keyOfGlobalMenuOpenOfLocalStorage = "GlobalMenuOpen-3a88471f-fb34-4a32-8847-1ffb34ea328f";

function main() {
    if (!existsWindow) {
        return;
    }
    setGlobalUserInfo();
    setGlobalMenuOpen();
    fromEvent(window, "storage").pipe(
        switchMap(() => {
            return from(setGlobalUserInfo());
        }),
        retry(),
    ).subscribe();
}

export default main()

