import { observable } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun";
import remote from '@/remote';

const GlobalState = observable({
    ready: false,
})

export function useReadyForApplication() {

    useMount(async () => {
        await remote.waitApplicationLoaded();
        GlobalState.ready = true
    })

    return GlobalState.ready;
}