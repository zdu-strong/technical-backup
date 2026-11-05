import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SuperAdminOrganizeManage from "@/component/super-admin/organize/dashboard/SuperAdminOrganizeManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <SuperAdminOrganizeManage />
</CheckPermissionComponent>