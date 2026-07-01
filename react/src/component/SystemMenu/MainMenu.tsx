import { observer } from 'mobx-react-use-autorun';
import { type ReactNode } from "react";
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@component/SystemMenu/UserInfoMenu';
import { useReactRouterAppProviderNavigation } from '@component/SystemMenu/js/useReactRouterAppProviderNavigation';
import { isMobilePhone } from "@/common/is-mobile-phone";
import { GlobalMenuOpen, setGlobalMenuOpen } from '@/common/Server';
import { style } from 'typestyle';

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
        branding={{ title: "", logo: "" }}
    >
        <div className={`flex flex-col flex-auto ${dashboardLayoutContainer}`}>
            <DashboardLayout
                slots={{ toolbarActions: UserInfoMenu }}
                slotProps={isMobilePhone ? undefined : {
                    header: {
                        menuOpen: GlobalMenuOpen.menuOpen,
                        onToggleMenu: setGlobalMenuOpen,
                        hideMenuButton: false,
                    }
                }}
                hideNavigation={isMobilePhone ? undefined : !GlobalMenuOpen.menuOpen}
            >
                <div className="flex flex-col flex-auto">
                    {props.children}
                </div>
            </DashboardLayout>
        </div>
    </ReactRouterAppProvider>
})
