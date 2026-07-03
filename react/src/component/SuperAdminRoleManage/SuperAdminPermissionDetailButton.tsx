import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v7 } from "uuid";
import { FormattedMessage } from "react-intl";
import SuperAdminPermissionDetailDialog from "@component/SuperAdminRoleManage/SuperAdminPermissionDetailDialog";

type Props = {
    permission: string;
}

export default observer((props: Props) => {

    const state = useMobxState({
        dialog: {
            open: false,
            id: v7()
        }
    })

    function openDialog() {
        state.dialog.id = v7();
        state.dialog.open = true;
    }

    function closeDialog() {
        state.dialog.open = false;
    }

    return <>
        <Button
            onClick={openDialog}
            variant="contained"
            startIcon={<FontAwesomeIcon icon={faCircleInfo} />}
            style={{ marginLeft: "1em" }}
        >
            <FormattedMessage id="Detail" defaultMessage="Detail" />
        </Button>
        {
            state.dialog.open && <SuperAdminPermissionDetailDialog
                key={state.dialog.id}
                closeDialog={closeDialog}
                permission={props.permission}
            />
        }
    </>
})