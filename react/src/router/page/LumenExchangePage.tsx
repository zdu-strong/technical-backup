import LumenExchange from "@/component/Lumen/LumenExchange";
import CheckPermissionComponent from "@common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@component/SystemMenu/MainMenu";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <LumenExchange />
  </MainMenu>
</CheckPermissionComponent>