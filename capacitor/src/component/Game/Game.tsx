import LoadingOrErrorComponent from '@common/MessageService/LoadingOrErrorComponent';
import { initGameEngine } from '@component/Game/js/initGameEngine';
import * as BABYLON from '@babylonjs/core';
import { observer, useMobxState, useMount } from 'mobx-react-use-autorun';
import { useRef } from 'react';
import { style } from 'typestyle';

const divContainer = style({
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    height: "100vh",
    $nest: {
        "& > canvas": {
            width: "100%",
            height: "100%",
        },
        "& > div": {
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            zIndex: 1000,
            backgroundColor: "white",
        }
    }
});

export default observer(() => {

    const state = useMobxState({
        engine: null as BABYLON.Engine | null,
        ready: false,
        error: null as any,
    }, {
        canvasRef: useRef<HTMLCanvasElement>(null),
    })

    useMount(async (subscription) => {
        try {
            state.engine = await initGameEngine(state.canvasRef, subscription);
            state.canvasRef.current!.focus();
            state.ready = true;
        } catch (error) {
            state.error = error
        }
    })


    return <>
        <div className={divContainer} style={state.ready ? {} : { position: "relative" }}>
            <canvas ref={state.canvasRef} style={{ outlineStyle: "none" }} />
            {!state.ready && <div
                className='flex flex-col flex-auto'
                style={{
                    paddingLeft: `15px`,
                    paddingRight: `15px`,
                    paddingTop: '5px',
                    paddingBottom: "5px",
                }}
            >
                <LoadingOrErrorComponent ready={state.ready} error={state.error} />
            </div>}
        </div>
    </>
})