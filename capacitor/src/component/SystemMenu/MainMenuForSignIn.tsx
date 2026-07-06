import { observer } from 'mobx-react-use-autorun';
import { type ReactNode } from "react";
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import ToolbarActionsMenuForSignIn from '@/component/SystemMenu/ToolbarActionsMenuForSignIn';
import { useReactRouterAppProviderNavigationForSignIn } from '@component/SystemMenu/js/useReactRouterAppProviderNavigationForSignIn';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faReact } from "@fortawesome/free-brands-svg-icons";
import { IconButton } from '@mui/material';
import { dashboardLayoutContainer } from "@component/SystemMenu/MainMenu";

type Props = {
    children: ReactNode;
}

export default observer((props: Props) => {

    const navigation = useReactRouterAppProviderNavigationForSignIn();

    return <ReactRouterAppProvider
        navigation={navigation}
        branding={{
            title: "",
            logo: <IconButton size="medium" color="primary"><FontAwesomeIcon icon={faReact} /></IconButton>,
            homeUrl: "/"
        }}
    >
        <div className={`flex flex-col flex-auto ${dashboardLayoutContainer}`}>
            <DashboardLayout
                slots={{ toolbarActions: ToolbarActionsMenuForSignIn }}
                defaultSidebarCollapsed={true}
            >
                <div className="flex flex-col flex-auto">
                    {props.children}
                </div>
            </DashboardLayout>
        </div>
    </ReactRouterAppProvider>
})
