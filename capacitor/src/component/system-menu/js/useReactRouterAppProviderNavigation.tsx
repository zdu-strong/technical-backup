import { type Navigation } from '@toolpad/core/AppProvider';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import BarChartIcon from '@mui/icons-material/BarChart';
import DescriptionIcon from '@mui/icons-material/Description';
import LayersIcon from '@mui/icons-material/Layers';
import { useCommonContext } from '@/common/CommonContext';
import { hasAnyPermission } from '@/common/Server';
import { SystemPermissionEnum } from '@/enums/SystemPermissionEnum';

export function useReactRouterAppProviderNavigation() {

    const { formatMessage } = useCommonContext()


    return [
        {
            kind: 'header',
            title: 'Main items',
        },
        {
            segment: 'dashboard',
            title: 'Dashboard',
            icon: <DashboardIcon />,
        },
        {
            segment: 'orders',
            title: 'Orders',
            icon: <ShoppingCartIcon />,
        },
        {
            segment: 'chat',
            title: formatMessage({ id: "Chat", defaultMessage: "Chat" }),
            icon: <ShoppingCartIcon />,
        },
        {
            segment: 'git',
            title: formatMessage({ id: "Git", defaultMessage: "Git" }),
            icon: <ShoppingCartIcon />,
        },
        ...(hasAnyPermission(SystemPermissionEnum.SUPER_ADMIN) ? [
            {
                kind: 'divider',
            },
            {
                kind: 'header',
                title: 'Super Administrator',
            },
            {
                segment: 'super-admin/role/manage',
                title: formatMessage({ id: "RoleManage", defaultMessage: "Role Manage" }),
                icon: <ShoppingCartIcon />,
            },
            {
                segment: 'super-admin/user/manage',
                title: formatMessage({ id: "UserManage", defaultMessage: "User Manage" }),
                icon: <ShoppingCartIcon />,
            },
            {
                segment: 'super-admin/organize/manage',
                title: formatMessage({ id: "OrganizeManage", defaultMessage: "Organize Manage" }),
                icon: <ShoppingCartIcon />,
            },
        ] : []),
        {
            kind: 'divider',
        },
        {
            kind: 'header',
            title: 'Analytics',
        },
        {
            segment: 'lumen',
            title: 'Lumen',
            icon: <BarChartIcon />,
            children: [
                {
                    segment: 'exchange',
                    title: 'Exchange',
                    icon: <DescriptionIcon />,
                },
            ],
        },
        {
            segment: 'report',
            title: 'Reports',
            icon: <BarChartIcon />,
            children: [
                {
                    segment: 'sales',
                    title: 'Sales',
                    icon: <DescriptionIcon />,
                },
                {
                    segment: 'traffic',
                    title: 'Traffic',
                    icon: <DescriptionIcon />,
                },
            ],
        },
        {
            segment: 'integrations',
            title: 'Integrations',
            icon: <LayersIcon />,
        },
    ] as Navigation;
}