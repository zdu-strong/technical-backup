import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import CountryDashboard from "@/component/CountryDashboard/CountryDashboard";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <CountryDashboard />
</CheckPermissionComponent>