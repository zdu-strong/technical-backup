import LoadingOrErrorComponent from '@common/MessageService/LoadingOrErrorComponent';
import { initGameEngine } from '@component/Game/js/initGameEngine';
import * as BABYLON from '@babylonjs/core';
import { observer, useMobxState, useMount } from 'mobx-react-use-autorun';
import { stylesheet } from 'typestyle';

const css = stylesheet({
  div: {
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
  }
})

export default observer((props: {
  canvasRef: React.RefObject<HTMLCanvasElement>,
}) => {

  const state = useMobxState({
    engine: null as BABYLON.Engine | null,
    ready: false,
    error: null as any,
  })

  useMount(async (subscription) => {
    try {
      state.engine = await initGameEngine(props.canvasRef, subscription);
      props.canvasRef.current!.focus();
      state.ready = true;
    } catch (error) {
      state.error = error
    }
  })

  return <>
    <div className={css.div} style={state.ready ? {} : { position: "relative" }}>
      <canvas ref={props.canvasRef} style={{ outlineStyle: "none" }} />
      {!state.ready && <LoadingOrErrorComponent ready={state.ready} error={state.error} />}
    </div>
  </>
})