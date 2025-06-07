import ExitButton from '@/component/Game/ExitButton';
import { Dialog } from '@mui/material';
import { observer, useMobxState } from 'mobx-react-use-autorun';
import { useRef } from 'react';
import Game from './Game';

export default observer((props: {
  closeDialog: () => void
}) => {

  const state = useMobxState({
  }, {
    canvasRef: useRef<HTMLCanvasElement>(null),
  })

  return <>
    <Dialog
      fullScreen
      open={true}
      onClose={props.closeDialog}
      disableRestoreFocus={true}
      disableEscapeKeyDown={true}
    >
      <ExitButton
        exit={props.closeDialog}
        canvasRef={state.canvasRef}
      />
      <Game
        canvasRef={state.canvasRef}
      />
    </Dialog>
  </>
})