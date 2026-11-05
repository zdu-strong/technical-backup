import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import RoleManage from "@/component/manage/role/dashboard/RoleManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <RoleManage />
</CheckPermissionComponent>