import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import SignUp from '@/component/sign-up/SignUp';

export default <CheckPermissionComponent
    checkIsNotSignIn={true}
>
    <SignUp />
</CheckPermissionComponent>