import { Button, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { style } from "typestyle";
import AccountTooltipDialog from "@component/SignIn/AccountTooltipDialog";
import PasswordTooltipDialog from "@component/SignIn/PasswordTooltipDialog";
import api from "@api";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleQuestion, faArrowRightToBracket, faSpinner, faHome, faUserPlus } from '@fortawesome/free-solid-svg-icons'
import { useOnceSubmitWhileTrue } from "@/common/use-hook";

const container = style({
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    paddingTop: "1em",
    paddingLeft: "5em",
    paddingRight: "5em",
});

export default observer(() => {

    const state = useMobxState({
        username: 'zdu.strong@gmail.com',
        password: 'zdu.strong@gmail.com',
        submitted: false,
        usernameTooltipDialog: {
            open: false,
        },
        passwordTooltipDialog: {
            open: false,
        },
        showPasswordInput: false,
        errors: {
            username() {
                if (state.username) {
                    if (state.username.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.username.length) {
                        return <FormattedMessage id="ThereShouldBeNoSpacesAtTheBeginningOfTheAccountId" defaultMessage="There should be no spaces at the beginning of the account ID" />
                    }
                    if (state.username.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.username.length) {
                        return <FormattedMessage id="TheAccountIDCannotHaveASpaceAtTheEnd" defaultMessage="The account ID cannot have a space at the end" />
                    }
                }
                if (!state.submitted) {
                    return false;
                }
                if (!state.username) {
                    return <FormattedMessage id="PleaseFillInTheAccountID" defaultMessage="Please fill in the account ID" />
                }
                return false;
            },
            password() {
                if (state.password) {
                    if (state.password.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.password.length) {
                        return <FormattedMessage id="PasswordMustNotHaveSpacesAtTheBeginning" defaultMessage="Password must not have spaces at the beginning" />
                    }
                    if (state.password.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.password.length) {
                        return <FormattedMessage id="PasswordCannotHaveASpaceAtTheEnd" defaultMessage="Password cannot have a space at the end" />
                    }
                }
                if (!state.submitted) {
                    return false;
                }
                if (!state.password) {
                    return <FormattedMessage id="PleaseFillInThePassword" defaultMessage="Please fill in the password" />
                }
                return false;
            },
            hasError() {
                return Object.keys(state.errors).filter(s => s !== "hasError").some(s => (state.errors as any)[s]());
            }
        },
    })

    const signIn = useOnceSubmitWhileTrue(async function () {
        if (!state.errors.hasError()) {
            state.showPasswordInput = false;
        }

        state.submitted = true;
        if (state.errors.hasError()) {
            return false;
        }
        await api.Authorization.signIn(state.username, state.password);
        return true;
    })

    function changeUsername(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        state.username = e.target.value;
    };

    function changePassword(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        state.password = e.target.value;
    };


    function showPasswordTooltip() {
        state.passwordTooltipDialog.open = true;
    };

    function showUsernameTooltip() {
        state.usernameTooltipDialog.open = true;
    };

    function closePasswordTooltip() {
        state.passwordTooltipDialog.open = false;
    };

    function closeUsernameTooltip() {
        state.usernameTooltipDialog.open = false;
    };

    function showPasswordInput() {
        state.showPasswordInput = true;
    };

    return <div className={container}>
        <div>
            <FormattedMessage id="SignIn" defaultMessage="SignIn" />
        </div>
        <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
            <TextField
                label={<FormattedMessage id="AccountID" defaultMessage="Account ID" />}
                variant="outlined"
                onChange={changeUsername}
                value={state.username}
                autoComplete="off"
                error={!!state.errors.username()}
                helperText={state.errors.username()}
                slotProps={{
                    input: {
                        endAdornment: <IconButton
                            color="primary"
                            onClick={showUsernameTooltip}
                        >
                            <FontAwesomeIcon icon={faCircleQuestion} />
                        </IconButton>
                    }
                }}
                autoFocus={true}
            />
        </div>
        <div style={{ marginTop: "1em" }} className="flex flex-col w-full">
            {state.showPasswordInput && <TextField
                label={<FormattedMessage id="Password" defaultMessage="Password" />}
                className="flex flex-auto"
                variant="outlined"
                onChange={changePassword}
                value={state.password}
                autoComplete="off"
                multiline={true}
                rows={6}
                error={!!state.errors.password()}
                helperText={state.errors.password()}
                slotProps={{
                    htmlInput: {
                        style: {
                            resize: "vertical",
                        }
                    },
                    input: {
                        endAdornment: <IconButton
                            color="primary"
                            onClick={showPasswordTooltip}
                        >
                            <FontAwesomeIcon icon={faCircleQuestion} />
                        </IconButton>
                    }
                }}
                autoFocus={true}
            />}
            {!state.showPasswordInput && <Button
                variant="outlined"
                className="w-full normal-case"
                onClick={showPasswordInput}
            >
                <FormattedMessage id="ThePasswordHasBeenFilledInClickEdit" defaultMessage="The password has been filled in, click Edit" />
            </Button>}
        </div>
        <div style={{ marginTop: "1em" }}>
            <Button
                variant="contained"
                className="normal-case"
                startIcon={<FontAwesomeIcon icon={signIn.loading ? faSpinner : faArrowRightToBracket} spin={signIn.loading} />}
                onClick={signIn.resubmit}
            >
                <FormattedMessage id="SignIn" defaultMessage="SignIn" />
            </Button>
        </div>
        <div className="w-full" style={{ marginTop: "2em" }}>
            <Link to="/sign-up" >
                <Button
                    variant="contained"
                    startIcon={<FontAwesomeIcon icon={faUserPlus} />}
                >
                    <FormattedMessage id="SignUp" defaultMessage="SignUp" />
                </Button>
            </Link>
            <Link to="/" style={{ marginLeft: "2em" }}>
                <Button
                    variant="contained"
                    startIcon={<FontAwesomeIcon icon={faHome} />}
                >
                    <FormattedMessage id="ReturnToHomePage" defaultMessage="To home" />
                </Button>
            </Link>
        </div>
        {state.usernameTooltipDialog.open && <AccountTooltipDialog
            closeDialog={closeUsernameTooltip}
        />}
        {state.passwordTooltipDialog.open && <PasswordTooltipDialog
            closeDialog={closePasswordTooltip}
        />}
    </div>;
})