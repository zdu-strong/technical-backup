import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Dialog, DialogContent, DialogTitle, Divider, Fab, Button } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { faArrowRightFromBracket, faSpinner, faUser } from '@fortawesome/free-solid-svg-icons';
import api from '@api';
import { GlobalUserInfo } from '@common/Server';
import { useOnceSubmit } from '@/common/use-hook';

type Props = {
    open: boolean;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const signOut = useOnceSubmit(async function () {
        await api.Authorization.signOut();
    });

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    return <>
        <Dialog
            open={props.open}
            onClose={closeDialog}
            disableRestoreFocus={true}
            fullWidth={true}
            maxWidth="md"
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    <FormattedMessage id="Setting" defaultMessage="Setting" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <div className="flex flex-col flex-auto" style={{ paddingBottom: "1px" }}>
                    <Button
                        variant="contained"
                        color="secondary"
                        startIcon={<FontAwesomeIcon icon={faUser} />}
                        style={{
                            marginTop: "1em",
                        }}
                    >
                        {GlobalUserInfo.username}
                    </Button>

                    <Button
                        variant="contained"
                        color="secondary"
                        startIcon={<FontAwesomeIcon icon={signOut.loading ? faSpinner : faArrowRightFromBracket} spin={signOut.loading} />}
                        onClick={signOut.resubmit}
                        style={{
                            marginTop: "1em"
                        }}
                    >
                        <FormattedMessage id="SignOut" defaultMessage="Sign out" />
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    </>
})