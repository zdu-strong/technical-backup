import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery, useOnceSubmit } from "@/common/use-hook";
import { faPenToSquare, faSpinner, faTrashCan, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { MessageService } from "@/common/MessageService";
import OrganizeDetail from "@component/SuperAdminOrganizeManage/OrganizeDetail";
import { OrganizeModel } from "@/model/OrganizeModel";
import { v4 } from "uuid";
import OrganizeCreateOrUpdateDialog from "@component/SuperAdminOrganizeManage/OrganizeCreateOrUpdateDialog";
import { OrganizeTypeEnum } from "@/enums/OrganizeTypeEnum";

type Props = {
    id: string;
    searchByPagination: () => void;
    closeDialog: () => void;
    isOnlyView: boolean;
}

export default observer((props: Props) => {

    const state = useMobxState({
        organize: new OrganizeModel(),
        updateDialog: {
            id: v4(),
            organizeId: "",
            organizeType: OrganizeTypeEnum.ORGANIZE.value,
            parentId: "",
            open: false,
        }
    });

    const { ready, error, requery } = useMultipleQuery(async () => {
        state.organize = await api.Organize.getOrganizeById(props.id);
    });

    const { loading, resubmit } = useOnceSubmit(async () => {
        await api.Organize.deleteOrganizeById(props.id);
        props.searchByPagination();
        props.closeDialog();
    })

    function confirmDeleteUser() {
        MessageService.confirm(<FormattedMessage
            id="AreYouSureDeleteOrganize"
            defaultMessage={`Are you sure you want to organize the organize "{name}"?`}
            values={{
                name: state.organize.name
            }}
        />, resubmit);
    }

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    function openCreateOrganizeDialog() {
        state.updateDialog.organizeId = "";
        state.updateDialog.organizeType = "";
        state.updateDialog.parentId = props.id;
        state.updateDialog.id = v4();
        state.updateDialog.open = true;
    }

    function openCreateRegionDialog() {
        state.updateDialog.organizeId = "";
        state.updateDialog.organizeType = OrganizeTypeEnum.GOVERNANCE_REGION.value;
        state.updateDialog.parentId = props.id;
        state.updateDialog.id = v4();
        state.updateDialog.open = true;
    }

    function openUpdateDialog() {
        state.updateDialog.organizeId = props.id;
        state.updateDialog.parentId = "";
        state.updateDialog.organizeType = "";
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
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    <FormattedMessage id="OrganizeDetail" defaultMessage="Organize Detail" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <LoadingOrErrorComponent ready={ready} error={error} >
                    <OrganizeDetail
                        organize={state.organize}
                    />
                </LoadingOrErrorComponent>
            </DialogContent>
            {ready && !props.isOnlyView && <>
                <Divider />
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={openCreateOrganizeDialog}
                        startIcon={<FontAwesomeIcon icon={faPenToSquare} />}
                    >
                        <FormattedMessage id="CreateOrganize" defaultMessage="Create Organize" />
                    </Button>
                    {[OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE].includes(OrganizeTypeEnum.parse(state.organize.organizeType)) && <Button
                        variant="contained"
                        onClick={openCreateRegionDialog}
                        startIcon={<FontAwesomeIcon icon={faPenToSquare} />}
                    >
                        <FormattedMessage id="CreateRegion" defaultMessage="Create Region" />
                    </Button>}
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
        {state.updateDialog.open && <OrganizeCreateOrUpdateDialog
            key={state.updateDialog.id}
            id={state.updateDialog.organizeId}
            organizeType={state.updateDialog.organizeType}
            parentId={state.updateDialog.parentId}
            searchByPagination={requeryOfUpdateDialog}
            closeDialog={closeUpdateDialog}
        />}
    </>
})