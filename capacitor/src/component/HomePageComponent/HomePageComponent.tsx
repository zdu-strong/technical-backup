import logo from '@component/HomePageComponent/image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Link } from "react-router-dom";
import { Button } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { useBatteryInfo } from '@component/HomePageComponent/js/useBatteryInfo';
import CircularProgress from '@mui/material/CircularProgress';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import GameDialog from '@component/Game/GameDialog';
import { App } from '@capacitor/app'
import { Capacitor } from '@capacitor/core';
import { faGamepad, faRightFromBracket, faWater } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

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

const divContainer = style({
    display: "flex",
    flexDirection: "column",
});

export default observer(() => {

    const state = useMobxState({
        gameDialog: {
            open: false,
        },
    }, {
        batteryInfo: useBatteryInfo(),
    });

    async function exit() {
        if (Capacitor.getPlatform() === "web") {
            return;
        }
        await App.exitApp()
    }

    return (<>
        <div
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
                        state.batteryInfo ? (<div>
                            <div>
                                {state.batteryInfo.isCharging ? (
                                    <FormattedMessage id="CurrentlyCharging" defaultMessage="当前正在充电" />
                                ) : (
                                    <FormattedMessage id="CurrentlyNotCharging" defaultMessage="当前没有充电" />
                                )}
                            </div>
                            <div>
                                <FormattedMessage id="CurrentBattery" defaultMessage="当前电量" />
                                {":" + Math.round(state.batteryInfo.batteryLevel! * 100) + "%"}
                            </div>
                        </div>) : (
                            <CircularProgress />
                        )
                    }
                    <div className={divContainer}>

                        <Link to="/not_found">
                            <Button
                                variant="contained"
                                startIcon={<FontAwesomeIcon icon={faWater} />}
                            >
                                <FormattedMessage id="toUnknownArea" defaultMessage="Go to the unknown area" />
                            </Button>
                        </Link>

                        <Button
                            variant="contained"
                            color="primary"
                            style={{ marginTop: "1em", fontSize: "large", paddingTop: "0", paddingBottom: "0" }}
                            startIcon={<FontAwesomeIcon icon={faGamepad} />}
                            onClick={() => {
                                state.gameDialog.open = true;
                            }}
                        >
                            <FormattedMessage id="EnterTheGameRightNowWithoutDoingTheExitButton" defaultMessage="Enter the game, right now, without doing the exit button" />
                        </Button>
                        <Button
                            variant="contained"
                            style={{
                                marginTop: "1em"
                            }}
                            onClick={exit}
                            startIcon={<FontAwesomeIcon icon={faRightFromBracket} />}
                        >
                            <FormattedMessage id="ExitTheApp" defaultMessage="Exit the app" />
                        </Button>
                    </div>
                </div>
            </header>
        </div>
        {state.gameDialog.open && <GameDialog closeDialog={() => {
            state.gameDialog.open = false;
        }} />}
    </>);
})
