import { observer, useMobxState } from "mobx-react-use-autorun";
import { DataGrid, type GridColDef, useGridApiRef } from '@mui/x-data-grid';
import { Box, Button } from "@mui/material";
import { format } from "date-fns";
import { AutoSizer } from "react-virtualized";
import api from "@api";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faSpinner } from "@fortawesome/free-solid-svg-icons";
import SuperAdminOrganizeDetailButton from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailButton";
import { FormattedMessage } from "react-intl";
import { PaginationModel } from "@model/PaginationModel";
import { OrganizeModel } from "@model/OrganizeModel";
import { SuperAdminOrganizeQueryPaginationModel } from "@model/SuperAdminOrganizeQueryPaginationModel";
import { useMultipleQuery } from "@/common/use-hook";

export default observer(() => {

    const state = useMobxState({
        query: new SuperAdminOrganizeQueryPaginationModel(),
        paginationModel: new PaginationModel<OrganizeModel>(),
        columns: [
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
                renderCell: (row) => <SuperAdminOrganizeDetailButton
                    id={row.row.id}
                    searchByPagination={organizeQueryState.requery}
                />,
                width: 150,
            },
        ] as GridColDef<OrganizeModel>[],
    });

    const dataGridRef = useGridApiRef();

    const organizeQueryState = useMultipleQuery(async () => {
        state.paginationModel = await api.SuperAdminOrganizeQuery.searchByPagination(state.query);
    });

    return <LoadingOrErrorComponent ready={organizeQueryState.ready} error={organizeQueryState.error}>
        <div className="flex flex-col flex-auto" style={{ paddingLeft: "50px", paddingRight: "50px" }}>
            <div className="flex flex-row" style={{ marginTop: "10px", marginBottom: "10px" }}>
                <Button
                    variant="contained"
                    onClick={organizeQueryState.requery}
                    startIcon={<FontAwesomeIcon icon={organizeQueryState.loading ? faSpinner : faSearch} spin={organizeQueryState.loading} />}
                >
                    <FormattedMessage id="Refresh" defaultMessage="Refresh" />
                </Button>
            </div>
            <div className="flex flex-auto" style={{ paddingBottom: "1px" }}>
                <AutoSizer>
                    {({ width, height }) => <Box width={Math.max(width, 100)} height={Math.max(height, 100)}>
                        <DataGrid
                            rows={state.paginationModel.items}
                            rowCount={state.paginationModel.totalRecords}
                            onPaginationModelChange={(s) => {
                                state.query.pageNum = Math.max(s.page + 1, 1);
                                state.query.pageSize = Math.max(s.pageSize, 1);
                                organizeQueryState.requery();
                            }}
                            apiRef={dataGridRef}
                            sortingMode="server"
                            paginationMode="server"
                            getRowId={(s) => s.id}
                            columns={state.columns}
                            autoPageSize
                            disableRowSelectionOnClick
                            disableColumnMenu
                            disableColumnResize
                            disableColumnSorting
                        />
                    </Box>}
                </AutoSizer>
            </div>
        </div>
    </LoadingOrErrorComponent>
})
