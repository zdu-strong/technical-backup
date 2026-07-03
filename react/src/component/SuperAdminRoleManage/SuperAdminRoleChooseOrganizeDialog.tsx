import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery } from "@/common/use-hook";
import { faSearch, faSpinner, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogContent, DialogTitle, Divider, Fab } from "@mui/material";
import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import { PaginationModel } from "@/model/PaginationModel";
import { SystemPermissionModel } from "@/model/SystemPermissionModel";
import { OrganizeModel } from "@/model/OrganizeModel";
import { SuperAdminOrganizeQueryPaginationModel } from "@/model/SuperAdminOrganizeQueryPaginationModel";
import { format } from "date-fns";
import SuperAdminOrganizeDetailButton from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailButton";
import { SystemPermissionEnum } from "@/enums/SystemPermissionEnum";
import { v4 } from "uuid";
import SuperAdminOrganizeChoosePermissionButton from "@component/SuperAdminRoleManage/SuperAdminOrganizeChoosePermissionButton";

type Props = {
    switchCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => void;
    isCheckedOfPermission: (systemPermissionModel: SystemPermissionModel) => boolean;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        const query = new SuperAdminOrganizeQueryPaginationModel();
        query.pageSize = 10;
        return {
            query: query,
            paginationModel: new PaginationModel<OrganizeModel>(),
            addDialog: {
                id: v4(),
                open: false,
                organize: new OrganizeModel(),
            }
        };
    });

    const organizeQueryState = useMultipleQuery(async () => {
        state.paginationModel = await api.SuperAdminOrganizeQuery.searchByPagination(state.query);
    });

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
            .filter(s => props.isCheckedOfPermission(s))
            .map(s => s.permission)
            .join(", ");
    }

    const columns: GridColDef<OrganizeModel>[] = [
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
            renderHeader: () => <FormattedMessage id="OrganizeType" defaultMessage="Organize Type" />,
            field: 'organizeType',
            width: 200,
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
            renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
            field: '',
            renderCell: (row) => <div className="flex flex-row items-center justify-between h-full">
                <SuperAdminOrganizeDetailButton
                    id={row.row.id}
                    searchByPagination={organizeQueryState.requery}
                    isOnlyView={true}
                />
                <SuperAdminOrganizeChoosePermissionButton
                    organize={row.row}
                    isCheckedOfPermission={props.isCheckedOfPermission}
                    switchCheckedOfPermission={props.switchCheckedOfPermission}
                    closeDialog={props.closeDialog}
                />
            </div>,
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

    useMobxEffect(() => {
        organizeQueryState.requery();
    }, [state.query])

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
                    <FormattedMessage id="AddOrganizePermission" defaultMessage="Add Organize Permission" />
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <LoadingOrErrorComponent ready={organizeQueryState.ready} error={organizeQueryState.error} >
                    <div className="flex flex-row" style={{ marginTop: "1em", marginBottom: "1em" }}>
                        <Button
                            variant="contained"
                            onClick={organizeQueryState.requery}
                            startIcon={<FontAwesomeIcon icon={organizeQueryState.loading ? faSpinner : faSearch} spin={organizeQueryState.loading} />}
                        >
                            <FormattedMessage id="Refresh" defaultMessage="Refresh" />
                        </Button>
                    </div>
                    <div className="flex flex-auto" style={{ paddingBottom: "1px" }}>
                        <DataGrid
                            rows={state.paginationModel.items}
                            rowCount={state.paginationModel.totalRecords}
                            onPaginationModelChange={(s) => {
                                state.query.pageNum = Math.max(s.page + 1, 1);
                                state.query.pageSize = Math.max(s.pageSize, 1);
                                organizeQueryState.requery();
                            }}
                            paginationModel={{ pageSize: state.query.pageSize, page: state.query.pageNum - 1 }}
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
                </LoadingOrErrorComponent>
            </DialogContent>
        </Dialog>
    </>
})