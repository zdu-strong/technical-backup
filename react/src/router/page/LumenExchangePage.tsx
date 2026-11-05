import LumenExchange from "@/component/lumen/LumenExchange";
import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <LumenExchange />
</CheckPermissionComponent>