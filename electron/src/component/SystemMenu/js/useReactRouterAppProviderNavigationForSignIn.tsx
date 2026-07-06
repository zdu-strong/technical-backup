import { type Navigation } from '@toolpad/core/AppProvider';
import DashboardIcon from '@mui/icons-material/Dashboard';
import { useCommonContext } from '@/common/CommonContext';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faUserPlus } from '@fortawesome/free-solid-svg-icons';

export function useReactRouterAppProviderNavigationForSignIn() {

    const { formatMessage } = useCommonContext()

    return [
        {
            kind: 'header',
            title: 'Main items',
        },
        {
            segment: 'dashboard',
            title: 'Home',
            icon: <DashboardIcon />,
        },
        {
            kind: 'divider',
        },
        {
            kind: 'header',
            title: 'User',
        },
        {
            segment: 'sign-in',
            title: formatMessage({ id: "SignIn", defaultMessage: "SignIn" }),
            icon: <FontAwesomeIcon icon={faUser} />,
        },
        {
            segment: 'sign-up',
            title: formatMessage({ id: "SignUp", defaultMessage: "SignUp" }),
            icon: <FontAwesomeIcon icon={faUserPlus} />,
        },
    ] as Navigation;
}