import { observer, useMobxState } from 'mobx-react-use-autorun';
import { Fab } from '@mui/material';
import { ThemeSwitcher } from '@toolpad/core/DashboardLayout';
import { Settings as SettingsIcon } from "@mui/icons-material";
import SettingDialogForSignIn from '@/component/SystemMenu/SettingDialogForSignIn';

export default observer(() => {

    const state = useMobxState({
        settingsDialog: {
            open: false
        }
    });

    function openSettingsDialog() {
        state.settingsDialog.open = true;
    }

    function closeSettingsDialog() {
        state.settingsDialog.open = false;
    }

    return <div className='flex flex-row items-center'>
        <ThemeSwitcher />

        <Fab
            color="secondary"
            aria-label="settings"
            size="small"
            style={{
                marginLeft: "1em",
            }}
            onClick={openSettingsDialog}
        >
            <SettingsIcon />
        </Fab>
        <SettingDialogForSignIn
            open={state.settingsDialog.open}
            closeDialog={closeSettingsDialog}
        />
    </div>
})