import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminUserManage from "@component/SuperAdminUserManage/SuperAdminUserManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminUserManage />
</CheckPermissionComponent>