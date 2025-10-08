import { Dialog, DialogContent, DialogContentText, DialogTitle, Divider, Fab } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faXmark } from '@fortawesome/free-solid-svg-icons'
import { FormattedMessage } from "react-intl";

type Props = {
    closeDialog: () => void;
}

export default observer((props: Props) => {

    return <Dialog
        open={true}
        onClose={props.closeDialog}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
    >
        <DialogTitle id="alert-dialog-title" className="flex flex-row justify-between items-center">
            <div>
                <FormattedMessage id="AccountTip" defaultMessage="Account tip" />
            </div>
            <Fab color="primary" size="small" aria-label="add" onClick={props.closeDialog} >
                <FontAwesomeIcon icon={faXmark} size="xl" />
            </Fab>
        </DialogTitle>
        <Divider />
        <DialogContent>
            <DialogContentText id="alert-dialog-description">
                <FormattedMessage id="UseAccountIDEmailOrMobilePhoneNumberToSignIn" defaultMessage="Use Account ID, email or mobile phone number to signIn" />
            </DialogContentText>
        </DialogContent>
    </Dialog>
})