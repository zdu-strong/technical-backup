import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import NotFound from "@/component/not-found/NotFound";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    {NotFound}
</CheckPermissionComponent>