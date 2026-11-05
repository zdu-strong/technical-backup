import { observable } from 'mobx-react-use-autorun';
import { useMount } from "mobx-react-use-autorun";
import remote from '@/remote';

const GlobalState = observable({
    ready: false,
    loading: false,
})

export function useReadyForApplication() {

    useMount(async () => {
        if (!GlobalState.loading) {
            GlobalState.loading = true;
            await remote.waitApplicationLoaded();
            GlobalState.loading = false;
            GlobalState.ready = true;
        }
    })

    return GlobalState.ready;
}