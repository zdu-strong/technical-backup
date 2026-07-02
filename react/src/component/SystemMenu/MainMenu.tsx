import { observer } from 'mobx-react-use-autorun';
import { type ReactNode } from "react";
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@component/SystemMenu/UserInfoMenu';
import { useReactRouterAppProviderNavigation } from '@component/SystemMenu/js/useReactRouterAppProviderNavigation';
import { isMobilePhone } from "@/common/is-mobile-phone";
import { style } from 'typestyle';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faReact } from "@fortawesome/free-brands-svg-icons";
import { IconButton } from '@mui/material';

const dashboardLayoutContainer = style({
    $nest: {
        "& > div.MuiBox-root": {
            width: "100%",
            height: "100%",
            flexDirection: "row",
            position: "static",
        },
        "& > div.MuiBox-root > header.MuiPaper-root": {
            position: "fixed",
            right: isMobilePhone ? "0px" : "15px",
            width: isMobilePhone ? "100vw" : "calc(100vw - 15px)",
        },
        "& > div.MuiBox-root > header.MuiPaper-root > div.MuiToolbar-root": {
            marginRight: "0px",
        },
        "& > div.MuiBox-root > div.MuiDrawer-root > div.MuiPaper-root": {
            height: "calc(100vh - 15px)"
        },
        "& > div.MuiBox-root > div.MuiDrawer-root.css-1hvrvza-MuiDrawer-docked": {
            width: "0px"
        },
        "& > div.MuiBox-root > div.MuiDrawer-root.css-1hvrvza-MuiDrawer-docked > div.MuiDrawer-paper": {
            width: "0px"
        },
        "& > div.MuiBox-root > div.MuiDrawer-root.css-1sbqulg-MuiDrawer-docked": {
            width: "0px"
        },
        "& > div.MuiBox-root > div.MuiDrawer-root.css-1sbqulg-MuiDrawer-docked > div.MuiDrawer-paper": {
            width: "0px"
        },
    },
    $debugName: "dashboardLayoutContainer"
});

type Props = {
    children: ReactNode;
}

export default observer((props: Props) => {

    const navigation = useReactRouterAppProviderNavigation();

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
                slots={{ toolbarActions: UserInfoMenu }}
                defaultSidebarCollapsed={true}
            >
                <div className="flex flex-col flex-auto">
                    {props.children}
                </div>
            </DashboardLayout>
        </div>
    </ReactRouterAppProvider>
})
