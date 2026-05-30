import { Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { style } from "typestyle";
import ExitDialog from "@component/Game/ExitDialog";
import { useMount } from "mobx-react-use-autorun";
import { Capacitor } from '@capacitor/core'
import { delay, from, of, repeat, Subscription } from "rxjs";
import { exhaustMapWithTrailing } from "rxjs-exhaustmap-with-trailing";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGear } from '@fortawesome/free-solid-svg-icons';
import { SafeArea } from '@aashu-dubey/capacitor-statusbar-safe-area';

const container = style({
    width: "100%",
    height: "0px",
    position: "relative",
    display: "flex",
    flexDirection: "row",
});

const exitButton = style({
    position: "absolute",
    top: "10px"
});

type Props = {
    exit: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState({
        exitDialog: {
            open: false,
        },
        ready: false,
        isLeftAndNotIsRightOfSafeArea: false,
        leftOrRightOfSafeArea: 10,
        topOfSafeArea: 10,
    })

    useMount(async (subscription) => {
        await loadSafeAreaInsets();
        await refreshSafeAreaInsets(subscription);
        state.ready = true;
    })

    async function loadSafeAreaInsets() {
        if (Capacitor.getPlatform() === "web") {
            return;
        }

        const safeArea = await SafeArea.getSafeAreaInsets();
        state.isLeftAndNotIsRightOfSafeArea = safeArea.right >= safeArea.left;
        state.topOfSafeArea = safeArea.top + 10;
        state.leftOrRightOfSafeArea = Math.min(safeArea.left, safeArea.right) + 10;
    }

    async function refreshSafeAreaInsets(subscription: Subscription) {
        if (Capacitor.getPlatform() === "web") {
            return;
        }

        subscription.add(of(null).pipe(
            exhaustMapWithTrailing(() => from((async () => {
                await loadSafeAreaInsets();
                return true;
            })())),
            delay(100),
            repeat(),
        ).subscribe());
    }

    return <>
        <div className={container}>
            {state.ready && <Fab
                size="small"
                color="primary"
                aria-label="add"
                style={state.isLeftAndNotIsRightOfSafeArea ? { left: `${state.leftOrRightOfSafeArea}px`, position: "absolute", top: `${state.topOfSafeArea}px` } : { right: `${state.leftOrRightOfSafeArea}px`, position: "absolute", top: `${state.topOfSafeArea}px` }}
                className={exitButton}
                onClick={() => {
                    state.exitDialog.open = true
                }}
            >
                <FontAwesomeIcon icon={faGear} size="xl" />
            </Fab>}
        </div>
        {state.exitDialog.open && <ExitDialog exit={props.exit} closeDialog={() => { state.exitDialog.open = false }} />}
    </>
})