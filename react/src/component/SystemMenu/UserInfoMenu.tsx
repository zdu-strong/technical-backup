import { observer } from 'mobx-react-use-autorun';
import { Button, Fab } from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowRightFromBracket, faSpinner, faUser } from '@fortawesome/free-solid-svg-icons';
import api from '@api';
import { FormattedMessage } from 'react-intl';
import { GlobalUserInfo } from '@common/Server';
import { ThemeSwitcher } from '@toolpad/core/DashboardLayout';
import { useOnceSubmit } from '@/common/use-hook';
import { isMobilePhone } from "@/common/is-mobile-phone";
import { Settings as SettingsIcon } from "@mui/icons-material";

export default observer(() => {

    const signOut = useOnceSubmit(async function () {
        await api.Authorization.signOut();
    });

    return <div className='flex flex-row items-center'>
        <ThemeSwitcher />

        {!isMobilePhone && <Button
            variant="contained"
            color="secondary"
            startIcon={<FontAwesomeIcon icon={faUser} />}
            style={{
                marginLeft: "1em",
            }}
        >
            {GlobalUserInfo.username}
        </Button>}

        <Button
            variant="contained"
            color="secondary"
            startIcon={<FontAwesomeIcon icon={signOut.loading ? faSpinner : faArrowRightFromBracket} spin={signOut.loading} />}
            onClick={signOut.resubmit}
            style={{
                marginLeft: "1em"
            }}
        >
            <FormattedMessage id="SignOut" defaultMessage="Sign out" />
        </Button>

        <Fab
            color="secondary"
            aria-label="settings"
            size="small"
            style={{
                marginLeft: "1em",
            }}
        >
            <SettingsIcon />
        </Fab>
    </div>
})