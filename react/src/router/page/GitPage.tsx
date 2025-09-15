import CheckPermissionComponent from "@common/checkPermission/CheckPagePermissionComponent";
import GitInfoComponent from "@component/GitInfo";
import MainMenu from "@component/SystemMenu/MainMenu";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <GitInfoComponent />
  </MainMenu>
</CheckPermissionComponent>