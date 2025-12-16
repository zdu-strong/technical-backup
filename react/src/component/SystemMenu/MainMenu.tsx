import { observer, useMobxState } from 'mobx-react-use-autorun';
import { type ReactNode } from "react";
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@component/SystemMenu/UserInfoMenu';
import { useReactRouterAppProviderNavigation } from '@component/SystemMenu/js/useReactRouterAppProviderNavigation';
import { style } from 'typestyle';

const dashboardLayoutContainer = style({
    $nest: {
        "& > div.MuiBox-root": {
            width: "100%",
            height: "100%"
        }
    }
});

type Props = {
    children: ReactNode;
}

export default observer((props: Props) => {

    const state = useMobxState({

    }, {
        navigation: useReactRouterAppProviderNavigation(),
    })

    return <ReactRouterAppProvider
        navigation={state.navigation}
        branding={{ title: "", logo: "" }}
    >
        <div className={`flex flex-col flex-auto ${dashboardLayoutContainer}`}>
            <DashboardLayout
                slots={{ toolbarActions: UserInfoMenu }}
            >
                <div className="overflow-auto flex flex-auto">
                    <div className="flex flex-col min-w-full min-h-full w-max">
                        <div className='flex flex-col flex-auto'>
                            {props.children}
                        </div>
                    </div>
                </div>
            </DashboardLayout>
        </div>
    </ReactRouterAppProvider>
})
