import CheckPermissionComponent from "@/common/Server/CheckPagePermissionComponent";
import CountryDashboard from "@/component/country-dashboard/CountryDashboard";

export default <CheckPermissionComponent
    checkIsSignIn={true}
>
    <CountryDashboard />
</CheckPermissionComponent>