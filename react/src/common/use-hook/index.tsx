import { useMobxState, useMount } from "mobx-react-use-autorun";
import { concatMap, exhaustMap, from, of, ReplaySubject, retry } from "rxjs";
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing';
import { MessageService } from "@/common/MessageService";

export function useMultipleQuery(callback: () => Promise<void>) {

    const subjectState = useMobxState({
        subject: new ReplaySubject<string>(1),
    });

    const state = useMobxState({
        loading: false,
        ready: false,
        error: null as any,
        requery,
    });

    function requery() {
        subjectState.subject.next("");
    }

    useMount(async (subscription) => {
        state.requery();
        subscription.add(subjectState.subject.pipe(
            exhaustMapWithTrailing(() => of(null).pipe(
                concatMap(() => from((async () => {
                    state.loading = true;
                    try {
                        await callback();
                        state.loading = false;
                        state.error = null;
                        state.ready = true;
                    } catch (e) {
                        state.loading = false;
                        state.error = e;
                    }
                })())),
            )),
            retry(),
        ).subscribe());
    })

    return state;
}

export function useMultipleSubmit(callback: () => Promise<void>) {

    const subjectState = useMobxState({
        subject: new ReplaySubject<string>(1),
    });

    const state = useMobxState({
        loading: false,
        ready: false,
        error: null as any,
        resubmit,
    });

    function resubmit() {
        subjectState.subject.next("");
    }

    useMount(async (subscription) => {
        subscription.add(subjectState.subject.pipe(
            exhaustMap(() => of(null).pipe(
                concatMap(() => from((async () => {
                    state.loading = true;
                    try {
                        await callback();
                        state.loading = false;
                        state.error = null;
                        state.ready = true;
                    } catch (e) {
                        state.loading = false;
                        state.error = e;
                        MessageService.error(e);
                    }
                })())),
            )),
            retry(),
        ).subscribe());
    })

    return state;
}

export function useOnceSubmit(callback: () => Promise<void>) {

    const subjectState = useMobxState({
        subject: new ReplaySubject<string>(1),
    });

    const state = useMobxState({
        loading: false,
        ready: false,
        error: null as any,
        resubmit,
    });

    function resubmit() {
        subjectState.subject.next("");
    }

    useMount(async (subscription) => {
        subscription.add(subjectState.subject.pipe(
            exhaustMapWithTrailing(() => of(null).pipe(
                concatMap(() => from((async () => {
                    if (state.ready) {
                        return;
                    }
                    state.loading = true;
                    try {
                        await callback();
                        state.loading = true;
                        state.error = null;
                        state.ready = true;
                    } catch (e) {
                        state.loading = false;
                        state.error = e;
                        MessageService.error(e);
                    }
                })())),
            )),
            retry(),
        ).subscribe());
    })

    return state;
}

export function useOnceSubmitWhileTrue(callback: () => Promise<boolean>) {

    const subjectState = useMobxState({
        subject: new ReplaySubject<string>(1),
    });

    const state = useMobxState({
        loading: false,
        ready: false,
        error: null as any,
        resubmit,
    });

    function resubmit() {
        subjectState.subject.next("");
    }

    useMount(async (subscription) => {
        subscription.add(subjectState.subject.pipe(
            exhaustMapWithTrailing(() => of(null).pipe(
                concatMap(() => from((async () => {
                    if (state.ready) {
                        return;
                    }
                    state.loading = true;
                    try {
                        const result = await callback();
                        if (result === true) {
                            state.loading = true;
                            state.error = null;
                            state.ready = true;
                        } else {
                            state.loading = false;
                            state.error = null;
                        }
                    } catch (e) {
                        state.loading = false;
                        state.error = e;
                        MessageService.error(e);
                    }
                })())),
            )),
            retry(),
        ).subscribe());
    })

    return state;
}