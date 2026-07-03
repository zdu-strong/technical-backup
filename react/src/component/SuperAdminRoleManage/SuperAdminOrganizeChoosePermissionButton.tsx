import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v7 } from "uuid";
import { FormattedMessage } from "react-intl";
import type { SystemPermissionModel } from "@/model/SystemPermissionModel";
import type { OrganizeModel } from "@/model/OrganizeModel";
import SuperAdminOrganizeChoosePermissionDialog from "@component/SuperAdminRoleManage/SuperAdminOrganizeChoosePermissionDialog";

type Props = {
    switchCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => void;
    isCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => boolean;
    organize: OrganizeModel;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState({
        dialog: {
            id: v7(),
            open: false,
        }
    })

    function openDialog() {
        state.dialog = {
            id: v7(),
            open: true,
        }
    }

    function closeDialog() {
        state.dialog.open = false;
        props.closeDialog();
    }

    return <>
        <Button
            variant="contained"
            onClick={openDialog}
            startIcon={<FontAwesomeIcon icon={faPlus} />}
            style={{ marginLeft: "1em" }}
        >
            <FormattedMessage id="AddPermission" defaultMessage="Add Permission" />
        </Button>
        {
            state.dialog.open && <SuperAdminOrganizeChoosePermissionDialog
                key={state.dialog.id}
                closeDialog={closeDialog}
                organize={props.organize}
                isCheckedOfPermission={props.isCheckedOfPermission}
                switchCheckedOfPermission={props.switchCheckedOfPermission}
            />
        }
    </>
})