import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { format } from "date-fns";
import type { SystemRoleModel } from "@/model/SystemRoleModel";
import { v4 } from "uuid";
import { SystemPermissionModel } from "@/model/SystemPermissionModel";
import { DataGrid, useGridApiRef, type GridColDef } from "@mui/x-data-grid";
import { SystemPermissionEnum } from "@/enums/SystemPermissionEnum";
import linq from 'linq';
import SuperAdminOrganizeDetailButton from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeDetailButton";
import type { OrganizeModel } from "@/model/OrganizeModel";
import SuperAdminOrganizeViewPermissionButton from "@component/SuperAdminRoleManage/SuperAdminOrganizeViewPermissionButton";
import SuperAdminPermissionDetailButton from "@component/SuperAdminRoleManage/SuperAdminPermissionDetailButton";

type Props = {
    role: SystemRoleModel;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        const allPermissionList = SystemPermissionEnum.values()
            .map(s => {
                const systemPermissionModel = new SystemPermissionModel();
                systemPermissionModel.id = v4();
                systemPermissionModel.permission = s.value;
                return systemPermissionModel;
            });
        return {
            allPermissionList,
        };
    });

    const permissionListOfSystem = linq.from(state.allPermissionList)
        .where(s => !SystemPermissionEnum.parse(s.permission).isOrganizePermission)
        .where(s => isCheckedOfPermission(s))
        .toArray();

    const organizeList = linq.from(props.role.permissionList)
        .where(s => SystemPermissionEnum.parse(s.permission).isOrganizePermission)
        .select(s => s.organize)
        .groupBy(s => s.id)
        .select(s => s.first())
        .orderBy(s => s.id)
        .orderBy(s => s.createDate)
        .toArray();

    const columnsOfSystemPermission: GridColDef<SystemPermissionModel>[] = [
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
            renderHeader: () => <FormattedMessage id="Operation" defaultMessage="Operation" />,
            field: '',
            renderCell: (row) => <div className="flex flex-row items-center justify-between h-full">
                <SuperAdminOrganizeDetailButton
                    id={row.row.id}
                    searchByPagination={() => { }}
                    isOnlyView={true}
                />
                <SuperAdminOrganizeViewPermissionButton
                    organize={row.row}
                    isCheckedOfPermission={isCheckedOfPermission}
                    switchCheckedOfPermission={() => { }}
                />
            </div>,
            width: 300,
        },
    ];

    const dataGridOfOrganizePermissionRef = useGridApiRef();

    const dataGridOfSystemPermissionRef = useGridApiRef();

    function isCheckedOfPermission(systemPermissionModel: SystemPermissionModel) {
        const permission = systemPermissionModel.permission;
        const organizeId: string = systemPermissionModel.organize ? systemPermissionModel.organize.id : "";
        const isChecked = linq.from(props.role.permissionList)
            .where(s => s.permission === permission)
            .any(s => !SystemPermissionEnum.parse(permission).isOrganizePermission || s.organize.id === organizeId);
        return isChecked;
    }

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

    return <>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="RoleName" defaultMessage="Role Name" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.role.name}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="ID" defaultMessage="ID" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.role.id}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="CreateDate" defaultMessage="Create Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.role.createDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="UpdateDate" defaultMessage="Update Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.role.updateDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
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
    </>
})