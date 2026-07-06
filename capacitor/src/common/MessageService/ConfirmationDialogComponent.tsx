import { faCircleCheck, faCircleXmark, faSpinner, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogActions, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import type { ReactNode } from "react";
import { FormattedMessage } from "react-intl";
import { timer } from "rxjs";

type Props = {
    title: ReactNode;
    closeDialog: () => void;
    confirmCallback: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState({
        loading: false
    });

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    async function confirmCallback() {
        state.loading = true;
        await timer(500).toPromise();
        props.closeDialog();
        props.confirmCallback();
    }

    return <>
        <Dialog
            open={true}
            onClose={closeDialog}
            disableRestoreFocus={true}
            fullWidth={true}
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    {props.title}
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogActions>
                <Button
                    variant="contained"
                    startIcon={<FontAwesomeIcon icon={faCircleXmark} size="xl" />}
                    onClick={props.closeDialog}
                >
                    <FormattedMessage id="Cancel" defaultMessage="Cancel" />
                </Button>
                <Button
                    variant="contained"
                    startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faCircleCheck} size="xl" spin={state.loading} />}
                    onClick={confirmCallback}
                >
                    <FormattedMessage id="Confirm" defaultMessage="Confirm" />
                </Button>
            </DialogActions>
        </Dialog>
    </>
})