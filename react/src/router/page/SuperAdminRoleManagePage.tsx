import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminRoleManage from "@/component/super-admin/role/dashboard/SuperAdminRoleManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminRoleManage />
</CheckPermissionComponent>