import logo from '@component/Home/image/logo.svg';
import { FormattedMessage } from "react-intl";
import { Button, CircularProgress } from "@mui/material";
import { keyframes, style } from 'typestyle';
import { useMobxState, observer, useMount } from 'mobx-react-use-autorun';
import { concatMap, from, of, repeat, timer } from 'rxjs';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faWater } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';

const container = style({
    textAlign: "center",
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
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
        randomNumber: null as number | null,
        people: {
            name: "",
        },
    })

    useMount((subscription) => {
        /* Generate random number */
        subscription.add(of(null).pipe(
            concatMap(() => from((async () => {
                while (true) {
                    const numberOne = Math.floor(Math.random() * 100 + 1);
                    if (state.randomNumber !== numberOne) {
                        state.randomNumber = numberOne;
                        break;
                    }
                    await timer(0).toPromise();
                }
            })())),
            repeat({ delay: 1000 }),
        ).subscribe());
    })

    return (
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
                        state.randomNumber !== null ? (<div>
                            <div>
                                <FormattedMessage id="RandomNumber" defaultMessage="Random number" />
                                {": " + (state.randomNumber!) + "%"}
                            </div>
                        </div>) : (
                            <CircularProgress />
                        )
                    }
                    <div>
                        <Link to="/not_found">
                            <Button
                                variant="contained"
                                startIcon={<FontAwesomeIcon icon={faWater} />}
                            >
                                <FormattedMessage id="GoToTheUnknownArea" defaultMessage="Go to the unknown area" />
                            </Button>
                        </Link>
                    </div>
                </div>
            </header>
        </div>
    );
})