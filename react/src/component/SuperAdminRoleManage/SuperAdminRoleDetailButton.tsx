import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { v7 } from "uuid";
import SuperAdminRoleDetailDialog from "@component/SuperAdminRoleManage/SuperAdminRoleDetailDialog";
import { FormattedMessage } from "react-intl";

export default observer((props: { id: string, searchByPagination: () => void }) => {

  const state = useMobxState({
    dialog: {
      open: false,
      id: v7()
    }
  })

  function openDialog() {
    state.dialog = {
      open: true,
      id: v7()
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
      state.dialog.open && <SuperAdminRoleDetailDialog
        id={props.id}
        searchByPagination={props.searchByPagination}
        key={state.dialog.id}
        closeDialog={closeDialog}
      />
    }
  </>
})