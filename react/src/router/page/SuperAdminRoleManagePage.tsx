import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminRoleManage from "@component/SuperAdminRoleManage/SuperAdminRoleManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminRoleManage />
</CheckPermissionComponent>