import logo from '@component/HomePageComponent/image/logo.svg';
import { FormattedMessage } from "react-intl";
import BootLoadingComponent from "@component/HomePageComponent/BootLoadingComponent";
import { Button } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { useRandomNumber } from '@component/HomePageComponent/js/useRandomNumber';
import { useReadyForApplication } from '@component/HomePageComponent/js/useReadyForApplication';
import CircularProgress from '@mui/material/CircularProgress';
import GameDialog from '@component/Game/GameDialog';
import remote from '@/remote';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGamepad } from '@fortawesome/free-solid-svg-icons';

const container = style({
    textAlign: "center",
});

const headerCss = style({
    backgroundColor: "#282c34",
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "calc(10px + 2vmin)",
    color: "white",
});

const imgAnimation = style({
    height: "40vmin",
    pointerEvents: "none",
    animationName: keyframes({
        "from": {
            transform: "rotate(0deg)"
        },
        "to": {
            transform: "rotate(360deg)",
        }
    }),
    animationDuration: "20s",
    animationIterationCount: "infinite",
    animationTimingFunction: "linear",
});

const batteryContainer = style({
    color: "#61dafb",
    display: "flex",
    flexDirection: "column",
});

export default observer(() => {

    const state = useMobxState({
        gameDialog: {
            open: false,
        },
    }, {
        randomNumber: useRandomNumber(),
        ready: useReadyForApplication(),
    })

    function closeDialog() {
        remote.getCurrentWindow().setFullScreen(false)
        remote.getCurrentWindow().setMenuBarVisibility(true)
        state.gameDialog.open = false;
    }

    function openDialog() {
        state.gameDialog.open = true;
        if (!remote.isNotShowForTest) {
            remote.getCurrentWindow().setMenuBarVisibility(false)
            remote.getCurrentWindow().setFullScreen(true)
        }
    }

    return (<>
        {!state.ready && BootLoadingComponent}
        {state.ready && <div
            className={container}
        >
            <header
                className={headerCss}
            >
                <img
                    src={logo}
                    className={imgAnimation} alt="logo" />
                <div className="flex">
                    <FormattedMessage id="EditSrcAppTsxAndSaveToReload" defaultMessage="Edit src/App.tsx and save to reload" />
                    {"."}
                </div>
                <div
                    className={batteryContainer}
                >
                    {
                        state.randomNumber !== null ? (<div className="flex flex-col">
                            <div className="text-center">
                                <FormattedMessage id="RandomNumber" defaultMessage="Random number" />
                                {`:  ${state.randomNumber}`}
                            </div>
                        </div>) : (
                            <div className="flex flex-row justify-center">
                                <CircularProgress />
                            </div>
                        )
                    }
                    <div>
                        <Button
                            variant="contained"
                            color="primary"
                            style={{ marginTop: "1em", fontSize: "large" }}
                            onClick={openDialog}
                            startIcon={<FontAwesomeIcon icon={faGamepad} />}
                        >
                            <FormattedMessage id="EnterTheGameIfYouWantToExitJustPressTheESCKey" defaultMessage="Enter the game, if you want to exit, just press the ESC key" />
                        </Button>
                    </div>
                </div>
            </header>
        </div>}
        {state.gameDialog.open && <GameDialog closeDialog={closeDialog} />}
    </>);
})