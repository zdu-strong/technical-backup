import ExitButton from '@component/Game/ExitButton';
import { Dialog } from '@mui/material';
import { observer } from 'mobx-react-use-autorun';
import { useRef } from 'react';
import Game from '@component/Game/Game';

type Props = {
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const canvasRef = useRef<HTMLCanvasElement>(null);

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
                canvasRef={canvasRef}
            />
            <Game
                canvasRef={canvasRef}
            />
        </Dialog>
    </>
})