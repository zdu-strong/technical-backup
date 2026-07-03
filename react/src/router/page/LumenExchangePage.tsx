import LumenExchange from "@/component/Lumen/LumenExchange";
import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <LumenExchange />
</CheckPermissionComponent>