import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery, useOnceSubmit } from "@/common/use-hook";
import { UserModel } from "@/model/UserModel";
import { faPenToSquare, faSpinner, faTrashCan, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import UserDetail from "@component/SuperAdminUserManage/UserDetail";
import { MessageService } from "@/common/MessageService";
import { v4 } from "uuid";
import UserCreateOrUpdateDialog from "@component/SuperAdminUserManage/UserCreateOrUpdateDialog";

type Props = {
    id: string;
    searchByPagination: () => void;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState({
        user: new UserModel(),
        updateDialog: {
            id: v4(),
            open: false,
        }
    });

    const { ready, error, requery } = useMultipleQuery(async () => {
        state.user = await api.User.getUserById(props.id);
    });

    const { loading, resubmit } = useOnceSubmit(async () => {
        await api.User.deleteUserById(props.id);
        props.searchByPagination();
        props.closeDialog();
    })

    function confirmDeleteUser() {
        MessageService.confirm(<FormattedMessage
            id="AreYouSureDeleteUser"
            defaultMessage={`Are you sure you want to delete the user "{username}"?`}
            values={{
                username: state.user.username
            }}
        />, resubmit);
    }

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    function openUpdateDialog() {
        state.updateDialog.id = v4();
        state.updateDialog.open = true;
    }

    function closeUpdateDialog() {
        state.updateDialog.open = false;
    }

    function requeryOfUpdateDialog() {
        requery();
        props.searchByPagination();
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
                    <FormattedMessage id="UserDetail" defaultMessage="User Detail" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <LoadingOrErrorComponent ready={ready} error={error} >
                    <UserDetail
                        user={state.user}
                    />
                </LoadingOrErrorComponent>
            </DialogContent>
            {ready && <>
                <Divider />
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={openUpdateDialog}
                        startIcon={<FontAwesomeIcon icon={faPenToSquare} />}
                    >
                        <FormattedMessage id="Update" defaultMessage="Update" />
                    </Button>
                    <Button
                        variant="contained"
                        onClick={confirmDeleteUser}
                        startIcon={<FontAwesomeIcon icon={loading ? faSpinner : faTrashCan} spin={loading} />}
                    >
                        <FormattedMessage id="Delete" defaultMessage="Delete" />
                    </Button>
                </DialogActions>
            </>}
        </Dialog>
        {state.updateDialog.open && <UserCreateOrUpdateDialog
            id={props.id}
            searchByPagination={requeryOfUpdateDialog}
            closeDialog={closeUpdateDialog}
        />}
    </>
})