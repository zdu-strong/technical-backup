import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import NotFound from "@component/NotFound/NotFound";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    {NotFound}
</CheckPermissionComponent>