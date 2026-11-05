import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SignIn from '@component/sign-in/SignIn';

export default <CheckPermissionComponent
    checkIsNotSignIn={true}
>
    <SignIn />
</CheckPermissionComponent>