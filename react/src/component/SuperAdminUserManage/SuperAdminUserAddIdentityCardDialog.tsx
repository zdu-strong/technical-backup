import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery } from "@/common/use-hook";
import { faCircleCheck, faSearch, faSpinner, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab, Radio } from "@mui/material";
import { observer, useMobxEffect, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import { PaginationModel } from "@/model/PaginationModel";
import { OrganizeModel } from "@/model/OrganizeModel";
import { SuperAdminOrganizeQueryPaginationModel } from "@/model/SuperAdminOrganizeQueryPaginationModel";
import { format } from "date-fns";
import SuperAdminOrganizeDetailButton from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailButton";
import { v4 } from "uuid";

type Props = {
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        const query = new SuperAdminOrganizeQueryPaginationModel();
        query.pageSize = 10;
        return {
            query: query,
            paginationModel: new PaginationModel<OrganizeModel>(),
            selectedOrganize: new OrganizeModel(),
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

    const columns: GridColDef<OrganizeModel>[] = [
        {
            renderHeader: () => "",
            field: 'checkbox',
            renderCell: (row) => <Radio
                checked={state.selectedOrganize.id === row.row.id}
                onChange={() => state.selectedOrganize = row.row}
                value="a"
                name="radio-buttons"
                inputProps={{ 'aria-label': row.row.name }}
            />,
            width: 70,
        },
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
                {/* <SuperAdminOrganizeChoosePermissionButton
                    organize={row.row}
                    isCheckedOfPermission={props.isCheckedOfPermission}
                    switchCheckedOfPermission={props.switchCheckedOfPermission}
                    closeDialog={props.closeDialog}
                /> */}
            </div>,
            width: 150,
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
                    <FormattedMessage id="AddIdentityCard" defaultMessage="Add Identity Card" />
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
            {organizeQueryState.ready && <>
                <Divider />
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={props.closeDialog}
                        disabled={!state.selectedOrganize.id}
                        startIcon={<FontAwesomeIcon icon={faCircleCheck} />}
                    >
                        <FormattedMessage id="Next" defaultMessage="Next" />
                    </Button>
                </DialogActions>
            </>}
        </Dialog>
    </>
})