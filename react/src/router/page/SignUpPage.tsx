import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SignUp from '@component/SignUp/SignUp';

export default <CheckPermissionComponent
    checkIsNotSignIn={true}
>
    <SignUp />
</CheckPermissionComponent>