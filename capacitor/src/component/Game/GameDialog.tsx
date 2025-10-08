import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Dialog } from '@mui/material';
import Game from '@component/Game/Game';
import { useMount } from "mobx-react-use-autorun";
import { LANDSCAPE, PORTRAIT_PRIMARY } from '@common/ScreenOrentation';
import ExitButton from '@component/Game/ExitButton';
import { Subscription, tap, timer } from 'rxjs';

type Props = {
    closeDialog: () => void;
}

export default observer((props: Props) => {
    const state = useMobxState({
        ready: false,
    })

    useMount(async (subscription) => {
        subscription.add(timer(0).pipe(
            tap(() => {
                LANDSCAPE();
                state.ready = true;
            })
        ).subscribe());

        subscription.add(new Subscription(() => {
            PORTRAIT_PRIMARY()
        }));
    })

    return <>
        <Dialog
            fullScreen
            open={true}
            onClose={props.closeDialog}
            disableRestoreFocus={true}
        >
            {state.ready && <ExitButton exit={props.closeDialog} />}
            <Game />
        </Dialog>
    </>
})