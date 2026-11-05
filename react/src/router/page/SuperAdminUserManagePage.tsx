import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminUserManage from "@/component/super-admin/user/dashboard/SuperAdminUserManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminUserManage />
</CheckPermissionComponent>