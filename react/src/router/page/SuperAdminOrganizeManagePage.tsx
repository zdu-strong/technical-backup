import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminOrganizeManage from "@component/SuperAdminOrganizeManage/SuperAdminOrganizeManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminOrganizeManage />
</CheckPermissionComponent>