import ExitDialog from "@component/Game/ExitDialog";
import { faGear } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Fab } from "@mui/material";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";
import { style } from "typestyle";

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
    canvasRef: React.RefObject<HTMLCanvasElement | null>;
}

export default observer((props: Props) => {

    const state = useMobxState({
        exitDialog: {
            open: false,
        },
        ready: false,
        leftOrRight: 10,

    })

    useMount(() => {
        state.ready = true;
    })

    return <>
        <div className={container}>
            {state.ready && <Fab
                size="small"
                color="primary"
                aria-label="add"
                style={{ right: `${state.leftOrRight}px`, position: "absolute" }}
                className={exitButton}
                onClick={() => {
                    state.exitDialog.open = true
                }}
            >
                <FontAwesomeIcon icon={faGear} size="xl" />
            </Fab>}
        </div>
        {state.exitDialog.open && <ExitDialog
            canvasRef={props.canvasRef}
            exit={props.exit}
            closeDialog={() => {
                state.exitDialog.open = false
            }}
        />}
    </>
})