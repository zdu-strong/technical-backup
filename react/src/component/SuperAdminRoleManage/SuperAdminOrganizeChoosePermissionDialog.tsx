import { faCircleCheck, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import { SystemPermissionModel } from "@/model/SystemPermissionModel";
import { OrganizeModel } from "@/model/OrganizeModel";
import { SystemPermissionEnum } from "@/enums/SystemPermissionEnum";
import { v4 } from "uuid";
import SuperAdminPermissionDetailButton from "@component/SuperAdminRoleManage/SuperAdminPermissionDetailButton";

type Props = {
    switchCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => void;
    isCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => boolean;
    closeDialog: () => void;
    organize: OrganizeModel;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        const allPermissionList = SystemPermissionEnum.values()
            .filter(s => s.isOrganizePermission)
            .map(s => {
                const systemPermissionModel = new SystemPermissionModel();
                systemPermissionModel.id = v4();
                systemPermissionModel.permission = s.value;
                systemPermissionModel.organize = props.organize;
                return systemPermissionModel;
            });
        return {
            allPermissionList: allPermissionList,
        };
    });

    const columns: GridColDef<SystemPermissionModel>[] = [
        {
            renderHeader: () => "",
            field: 'checkbox',
            renderCell: (row) => <Checkbox
                checked={props.isCheckedOfPermission(row.row)}
                onChange={(e) => props.switchCheckedOfPermission(row.row)}
            />,
            width: 70,
        },
        {
            headerName: 'ID',
            field: 'id',
            width: 290
        },
        {
            renderHeader: () => <FormattedMessage id="Permission" defaultMessage="Permission" />,
            field: 'permission',
            width: 150,
            flex: 1,
        },
        {
            renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
            field: '',
            renderCell: (row) => <SuperAdminPermissionDetailButton
                permission={row.row.permission}
            />,
            width: 300,
        },
    ];

    const dataGridRef = useGridApiRef();

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
            fullScreen={true}
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    <FormattedMessage id="AddPermission" defaultMessage="Add Permission" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <div className="flex flex-auto" style={{ paddingBottom: "1px" }}>
                    <DataGrid
                        rows={state.allPermissionList}
                        rowCount={state.allPermissionList.length}
                        apiRef={dataGridRef}
                        sortingMode="server"
                        paginationMode="server"
                        getRowId={(s) => s.id}
                        columns={columns}
                        disableRowSelectionOnClick
                        disableColumnMenu
                        disableColumnResize
                        disableColumnSorting
                        autoHeight
                    />
                </div>
            </DialogContent>
            <Divider />
            <DialogActions>
                <Button
                    variant="contained"
                    onClick={props.closeDialog}
                    startIcon={<FontAwesomeIcon icon={faCircleCheck} />}
                >
                    <FormattedMessage id="Confirm" defaultMessage="Confirm" />
                </Button>
            </DialogActions>
        </Dialog>
    </>
})