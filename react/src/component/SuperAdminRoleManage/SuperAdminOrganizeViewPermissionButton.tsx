import { faUserShield } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v7 } from "uuid";
import { FormattedMessage } from "react-intl";
import type { SystemPermissionModel } from "@/model/SystemPermissionModel";
import type { OrganizeModel } from "@/model/OrganizeModel";
import SuperAdminOrganizeViewPermissionDialog from "@component/SuperAdminRoleManage/SuperAdminOrganizeViewPermissionDialog";

type Props = {
    switchCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => void;
    isCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => boolean;
    organize: OrganizeModel;
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
    }

    return <>
        <Button
            variant="contained"
            onClick={openDialog}
            startIcon={<FontAwesomeIcon icon={faUserShield} />}
            style={{ marginLeft: "1em" }}
        >
            <FormattedMessage id="Permission" defaultMessage="Permission" />
        </Button>
        {
            state.dialog.open && <SuperAdminOrganizeViewPermissionDialog
                key={state.dialog.id}
                closeDialog={closeDialog}
                organize={props.organize}
                isCheckedOfPermission={props.isCheckedOfPermission}
            />
        }
    </>
})