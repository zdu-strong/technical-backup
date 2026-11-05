import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import GitInfoComponent from "@/component/git-info";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <GitInfoComponent />
</CheckPermissionComponent>