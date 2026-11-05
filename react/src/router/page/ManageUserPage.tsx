import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import UserManage from "@/component/manage/user/dashboard/UserManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <UserManage />
</CheckPermissionComponent>