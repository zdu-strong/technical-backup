import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import OrganizeManage from "@/component/manage/organize/dashboard/OrganizeManage";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <OrganizeManage />
</CheckPermissionComponent>