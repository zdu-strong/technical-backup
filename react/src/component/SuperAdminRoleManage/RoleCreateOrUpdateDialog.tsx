import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery, useOnceSubmitWhileTrue } from "@/common/use-hook";
import { faFloppyDisk, faPlus, faSpinner, faSquarePlus, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { SystemRoleModel } from "@/model/SystemRoleModel";
import { timer } from "rxjs";
import { SystemPermissionEnum } from "@/enums/SystemPermissionEnum";
import linq from 'linq';
import { SystemPermissionModel } from "@/model/SystemPermissionModel";
import { v4 } from "uuid";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import SuperAdminRoleChooseOrganizeDialog from "@/component/SuperAdminRoleManage/SuperAdminRoleChooseOrganizeDialog";
import { OrganizeModel } from "@/model/OrganizeModel";
import SuperAdminOrganizeDetailButton from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailButton";
import { format } from 'date-fns';
import SuperAdminOrganizeChoosePermissionDialog from "@component/SuperAdminRoleManage/SuperAdminOrganizeChoosePermissionDialog";
import SuperAdminPermissionDetailButton from "@component/SuperAdminRoleManage/SuperAdminPermissionDetailButton";

type Props = {
    id: string;
    searchByPagination: () => void;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        const role = new SystemRoleModel();
        role.permissionList = [];
        const allPermissionList = SystemPermissionEnum.values()
            .map(s => {
                const systemPermissionModel = new SystemPermissionModel();
                systemPermissionModel.id = v4();
                systemPermissionModel.permission = s.value;
                return systemPermissionModel;
            });
        return {
            role: role,
            submit: false,
            allPermissionList,
            addDialog: {
                id: v4(),
                open: false,
            },
            permissionDialog: {
                id: v4(),
                open: false,
                organize: new OrganizeModel(),
            }
        };
    });

    const permissionListOfSystem = linq.from(state.allPermissionList)
        .where(s => !SystemPermissionEnum.parse(s.permission).isOrganizePermission)
        .orderByDescending(s => isCheckedOfPermission(s))
        .toArray();

    const organizeList = linq.from(state.role.permissionList)
        .where(s => SystemPermissionEnum.parse(s.permission).isOrganizePermission)
        .select(s => s.organize)
        .groupBy(s => s.id)
        .select(s => s.first())
        .orderBy(s => s.id)
        .orderBy(s => s.createDate)
        .toArray();

    const errors = {
        name() {
            return state.submit &&
                !state.role.name &&
                "Please fill in the name";
        },
        hasError() {
            return Object.keys(errors)
                .filter(s => s !== "hasError")
                .some(s => (errors as any)[s]());
        }
    };

    function openAddDialog() {
        state.addDialog.id = v4();
        state.addDialog.open = true;
    }

    function closeAddDialog() {
        state.addDialog.open = false;
    }

    const columnsOfSystemPermission: GridColDef<SystemPermissionModel>[] = [
        {
            renderHeader: () => "",
            field: 'checkbox',
            renderCell: (row) => <Checkbox
                checked={isCheckedOfPermission(row.row)}
                onChange={(e) => switchCheckedOfPermission(row.row)}
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
            width: 230,
        },
    ];

    const columnsOfOrganizePermission: GridColDef<OrganizeModel>[] = [
        {
            headerName: 'ID',
            field: 'id',
            width: 290
        },
        {
            renderHeader: () => <FormattedMessage id="Name" defaultMessage="Name" />,
            field: 'name',
            width: 150,
            flex: 1,
        },
        {
            renderHeader: () => <FormattedMessage id="Permission" defaultMessage="Permission" />,
            field: 'permissionList',
            renderCell: (row) => {
                return <div>
                    {getPermissionsName(row.row)}
                </div>
            },
            width: 400,
        },
        {
            renderHeader: () => <FormattedMessage id="CreateDate" defaultMessage="Create Date" />,
            field: 'createDate',
            renderCell: (row) => {
                return <div>
                    {format(row.row.createDate, "yyyy-MM-dd HH:mm:ss")}
                </div>
            },
            width: 150,
        },
        {
            renderHeader: () => <Button
                variant="contained"
                onClick={openAddDialog}
                startIcon={<FontAwesomeIcon icon={faSquarePlus} />}
                style={{ marginLeft: "1em" }}
                size="small"
            >
                <FormattedMessage id="Add" defaultMessage="Add" />
            </Button>,
            field: '',
            renderCell: (row) => <div className="flex flex-row items-center justify-between h-full">
                <SuperAdminOrganizeDetailButton
                    id={row.row.id}
                    searchByPagination={() => { }}
                    isOnlyView={true}
                />
                <Button
                    variant="contained"
                    onClick={() => openPermissionDialog(row.row)}
                    startIcon={<FontAwesomeIcon icon={faPlus} />}
                    style={{ marginLeft: "1em" }}
                >
                    <FormattedMessage id="AddPermission" defaultMessage="Add Permission" />
                </Button>
            </div>,
            width: 300,
        },
    ];

    const dataGridOfOrganizePermissionRef = useGridApiRef();

    const dataGridOfSystemPermissionRef = useGridApiRef();

    const { ready, error, requery } = useMultipleQuery(async () => {
        if (props.id) {
            state.role = await api.Role.getRoleById(props.id);
        }
    });

    const { loading, resubmit } = useOnceSubmitWhileTrue(async () => {
        state.submit = true;
        if (errors.hasError()) {
            return false;
        }
        await timer(500).toPromise();
        if (props.id) {
            await api.Role.updateRole(state.role);
        } else {
            await api.Role.createRole(state.role);
        }

        props.searchByPagination();
        props.closeDialog();
        return true;
    })

    function getPermissionsName(orgainze: OrganizeModel) {
        return SystemPermissionEnum.values()
            .filter(s => s.isOrganizePermission)
            .map(s => {
                const systemPermissionModel = new SystemPermissionModel();
                systemPermissionModel.id = v4();
                systemPermissionModel.organize = orgainze;
                systemPermissionModel.permission = s.value;
                return systemPermissionModel;
            })
            .filter(s => isCheckedOfPermission(s))
            .map(s => s.permission)
            .join(", ");
    }

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    function switchCheckedOfPermission(systemPermissionModel: SystemPermissionModel) {
        const permission = systemPermissionModel.permission;
        const organizeId: string = systemPermissionModel.organize ? systemPermissionModel.organize.id : "";
        if (!SystemPermissionEnum.parse(permission).isOrganizePermission) {
            const index = state.role.permissionList.findIndex(s => !SystemPermissionEnum.parse(s.permission).isOrganizePermission && s.permission === permission);
            if (index >= 0) {
                state.role.permissionList.splice(index, 1);
            } else {
                state.role.permissionList.push(systemPermissionModel);
            }
        } else {
            const index = state.role.permissionList.findIndex(s => SystemPermissionEnum.parse(s.permission).isOrganizePermission && s.organize.id === organizeId && s.permission === permission);
            if (index >= 0) {
                state.role.permissionList.splice(index, 1);
            } else {
                state.role.permissionList.push(systemPermissionModel);
            }
        }
    }

    function isCheckedOfPermission(systemPermissionModel: SystemPermissionModel) {
        const permission = systemPermissionModel.permission;
        const organizeId: string = systemPermissionModel.organize ? systemPermissionModel.organize.id : "";
        const isChecked = linq.from(state.role.permissionList)
            .where(s => s.permission === permission)
            .any(s => !SystemPermissionEnum.parse(s.permission).isOrganizePermission || s.organize.id === organizeId);
        return isChecked;
    }

    function openPermissionDialog(organize: OrganizeModel) {
        state.permissionDialog.organize = organize;
        state.permissionDialog.id = v4()
        state.permissionDialog.open = true;
    }

    function closePermissionDialog() {
        state.permissionDialog.open = false;
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
                    {props.id
                        ? <FormattedMessage id="Update" defaultMessage="Update" />
                        : <FormattedMessage id="Create" defaultMessage="Create" />}
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <LoadingOrErrorComponent ready={ready} error={error} >
                    <div className="flex flex-row">
                        <TextField
                            label={<FormattedMessage id="RoleName" defaultMessage="Role Name" />}
                            defaultValue={state.role.name}
                            onChange={(e) => state.role.name = e.target.value}
                            error={!!errors.name()}
                            helperText={errors.name()}
                            autoFocus={true}
                        />
                    </div>
                    <div className="flex flex-row">
                        <FormattedMessage id="SystemPermission" defaultMessage="System Permission" />
                        {":"}
                    </div>
                    <div className="flex flex-row" style={{ paddingBottom: "1px" }}>
                        <DataGrid
                            rows={permissionListOfSystem}
                            rowCount={permissionListOfSystem.length}
                            apiRef={dataGridOfSystemPermissionRef}
                            sortingMode="server"
                            paginationMode="server"
                            getRowId={(s) => s.id}
                            columns={columnsOfSystemPermission}
                            hideFooter
                            disableRowSelectionOnClick
                            disableColumnMenu
                            disableColumnResize
                            disableColumnSorting
                        />
                    </div>
                    <div className="flex flex-row">
                        <FormattedMessage id="OrganizePermission" defaultMessage="Organize Permission" />
                        {":"}
                    </div>
                    <div className="flex flex-row" style={{ paddingBottom: "1px" }}>
                        <DataGrid
                            rows={organizeList}
                            rowCount={organizeList.length}
                            apiRef={dataGridOfOrganizePermissionRef}
                            sortingMode="server"
                            paginationMode="server"
                            getRowId={(s) => s.id}
                            columns={columnsOfOrganizePermission}
                            hideFooter
                            disableRowSelectionOnClick
                            disableColumnMenu
                            disableColumnResize
                            disableColumnSorting
                        />
                    </div>
                </LoadingOrErrorComponent>
            </DialogContent>
            {ready && <>
                <Divider />
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={resubmit}
                        startIcon={<FontAwesomeIcon icon={loading ? faSpinner : faFloppyDisk} spin={loading} />}
                    >
                        <FormattedMessage id="Save" defaultMessage="Save" />
                    </Button>
                </DialogActions>
            </>}
        </Dialog>
        {state.addDialog.open && <SuperAdminRoleChooseOrganizeDialog
            closeDialog={closeAddDialog}
            isCheckedOfPermission={isCheckedOfPermission}
            switchCheckedOfPermission={switchCheckedOfPermission}
        />}
        {
            state.permissionDialog.open && <SuperAdminOrganizeChoosePermissionDialog
                key={state.permissionDialog.id}
                closeDialog={closePermissionDialog}
                organize={state.permissionDialog.organize}
                isCheckedOfPermission={isCheckedOfPermission}
                switchCheckedOfPermission={switchCheckedOfPermission}
            />
        }
    </>
})