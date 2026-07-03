import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SignIn from '@component/SignIn/SignIn';

export default <CheckPermissionComponent
    checkIsNotSignIn={true}
>
    <SignIn />
</CheckPermissionComponent>