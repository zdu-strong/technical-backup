import { UserModel } from "@/model/UserModel";
import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { format } from "date-fns";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import type { SystemRoleModel } from "@/model/SystemRoleModel";
import SuperAdminRoleDetailButton from "@component/SuperAdminRoleManage/SuperAdminRoleDetailButton";
import type { IdentityCardModel } from "@/model/IdentityCardModel";

type Props = {
    user: UserModel;
}

export default observer((props: Props) => {

    const dataGridRef = useGridApiRef();

    const columnsOfRoles: GridColDef<SystemRoleModel>[] = [
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
            renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
            field: '',
            renderCell: (row) => <SuperAdminRoleDetailButton
                id={row.row.id}
                searchByPagination={() => { }}
                isOnlyView={true}
            />,
            width: 150,
        },
    ];

    const columnsOfIdentityCardList: GridColDef<IdentityCardModel>[] = [
        {
            headerName: 'ID',
            field: 'id',
            width: 290
        },
        {
            renderHeader: () => <FormattedMessage id="OrganizeName" defaultMessage="Organize Name" />,
            field: 'organize',
            width: 150,
            flex: 1,
        },
        {
            renderHeader: () => <FormattedMessage id="OrganizeType" defaultMessage="Organize Type" />,
            field: 'organizeType',
            renderCell: (row) => {
                return <div>
                    {row.row.topOrganize.organizeType}
                </div>
            },
            width: 150,
        },
        {
            renderHeader: () => <FormattedMessage id="IdentityType" defaultMessage="Identity Type" />,
            field: 'identityType',
            renderCell: (row) => {
                return <div>
                    {row.row.identityType}
                </div>
            },
            width: 150,
        },
        {
            renderHeader: () => <FormattedMessage id="GovernanceRegion" defaultMessage="Governance Region" />,
            field: 'governanceRegion',
            renderCell: (row) => {
                return <div>
                    {row.row.governanceRegion.name}
                </div>
            },
            width: 150,
        },
        {
            renderHeader: () => <FormattedMessage id="Address" defaultMessage="Address" />,
            field: 'address',
            width: 150,
        },
        {
            renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
            field: '',
            renderCell: (row) => <SuperAdminRoleDetailButton
                id={row.row.id}
                searchByPagination={() => { }}
                isOnlyView={true}
            />,
            width: 150,
        },
    ];

    return <>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="Username" defaultMessage="Username" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.user.username}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="ID" defaultMessage="ID" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.user.id}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="CreateDate" defaultMessage="Create Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.user.createDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="UpdateDate" defaultMessage="Update Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.user.updateDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
        </div>
        <div className="flex flex-row">
            <FormattedMessage id="RoleList" defaultMessage="Role List" />
            {":"}
        </div>
        <div className="flex flex-row" style={{ paddingBottom: "1px" }}>
            <DataGrid
                rows={props.user.roleList}
                rowCount={props.user.roleList.length}
                apiRef={dataGridRef}
                sortingMode="server"
                paginationMode="server"
                getRowId={(s) => s.id}
                columns={columnsOfRoles}
                hideFooter
                disableRowSelectionOnClick
                disableColumnMenu
                disableColumnResize
                disableColumnSorting
            />
        </div>
        <div className="flex flex-row">
            <FormattedMessage id="IdentityCardList" defaultMessage="Identity Card List" />
            {":"}
        </div>
        <div className="flex flex-row" style={{ paddingBottom: "1px" }}>
            <DataGrid
                rows={props.user.identityCardList}
                rowCount={props.user.identityCardList.length}
                sortingMode="server"
                paginationMode="server"
                getRowId={(s) => s.id}
                columns={columnsOfIdentityCardList}
                hideFooter
                disableRowSelectionOnClick
                disableColumnMenu
                disableColumnResize
                disableColumnSorting
            />
        </div>
    </>
})