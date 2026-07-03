import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import GitInfoComponent from "@component/GitInfo";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <GitInfoComponent />
</CheckPermissionComponent>