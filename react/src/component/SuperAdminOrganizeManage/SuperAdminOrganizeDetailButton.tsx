import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v7 } from "uuid";
import SuperAdminOrganizeDetailDialog from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailDialog";
import { FormattedMessage } from "react-intl";

type Props = {
    id: string;
    searchByPagination: () => void;
    isOnlyView: boolean;
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
            onClick={openDialog}
            variant="contained"
            startIcon={<FontAwesomeIcon icon={faCircleInfo} />}
        >
            <FormattedMessage id="Detail" defaultMessage="Detail" />
        </Button>
        {
            state.dialog.open && <SuperAdminOrganizeDetailDialog
                id={props.id}
                searchByPagination={props.searchByPagination}
                key={state.dialog.id}
                closeDialog={closeDialog}
                isOnlyView={props.isOnlyView}
            />
        }
    </>
})