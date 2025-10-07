import { useMobxState, useMount } from "mobx-react-use-autorun";
import { concatMap, from, of, ReplaySubject, retry } from "rxjs";
import { exhaustMapWithTrailing } from 'rxjs-exhaustmap-with-trailing';
import { MessageService } from "../MessageService";

export function useQuery(callback: () => void) {

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
    subscription.add(subjectState.subject.pipe(
      exhaustMapWithTrailing(() => of(null).pipe(
        concatMap(() => from((async () => {
          state.loading = true;
          try {
            await callback();
            state.ready = true;
            state.loading = false;
            state.error = null;
          } catch (e) {
            state.error = e;
          }
        })())),
      )),
      retry(),
    ).subscribe());
  })

  state.requery();

  return state;
}

export function useMultipleSubmit(callback: () => void) {

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
          state.loading = true;
          try {
            await callback();
            state.ready = true;
            state.loading = false;
            state.error = null;
          } catch (e) {
            MessageService.error(e);
            state.error = e;
          }
        })())),
      )),
      retry(),
    ).subscribe());
  })

  return state;
}

export function useOnceSubmit(callback: () => void) {

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
    subscription.add(subjectState.subject.pipe(
      exhaustMapWithTrailing(() => of(null).pipe(
        concatMap(() => from((async () => {
          if (state.ready) {
            return;
          }
          state.loading = true;
          try {
            await callback();
            state.ready = true;
            state.loading = false;
            state.error = null;
          } catch (e) {
            MessageService.error(e);
            state.error = e;
          }
        })())),
      )),
      retry(),
    ).subscribe());
  })

  return state;
}