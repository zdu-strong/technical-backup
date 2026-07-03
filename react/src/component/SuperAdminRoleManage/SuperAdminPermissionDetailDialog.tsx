import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Dialog, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";

type Props = {
    permission: string;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    return <>
        <Dialog
            open={true}
            onClose={closeDialog}
            disableRestoreFocus={true}
            fullWidth={true}
            maxWidth="xl"
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    <FormattedMessage id="PermissionDetail" defaultMessage="Permission Detail" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <div className="flex flex-row">
                    {props.permission}
                </div>
            </DialogContent>
        </Dialog>
    </>
})