import { Button, Divider, Fab, IconButton, TextField, Typography } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { style } from "typestyle";
import { v7 } from "uuid";
import Box from '@mui/material/Box';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import api from "@api";
import { MessageService } from "@common/MessageService";
import { UserEmailModel } from "@model/UserEmailModel";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFloppyDisk, faPaperPlane, faPlus, faSpinner, faTrash } from "@fortawesome/free-solid-svg-icons";
import { useOnceSubmit } from "@/common/use-hook";

const container = style({
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    flex: "1 1 auto",
    width: "100%",
    paddingLeft: "5em",
    paddingRight: "5em",
});

export default observer(() => {

    const state = useMobxState({
        username: '',
        password: '',
        emailList: [] as UserEmailModel[],
        steps: [
            {
                id: v7(),
                text: <FormattedMessage id="SetUsername" defaultMessage="Set username" />,
            },
            {
                id: v7(),
                text: <FormattedMessage id="SetPassword" defaultMessage="Set password" />,
            },
            {
                id: v7(),
                text: <FormattedMessage id="BindEmail" defaultMessage="Bind email" />,
            },
            {
                id: v7(),
                text: <FormattedMessage id="Complete" defaultMessage="Complete" />,
            }
        ],
        activeStep: 0,
        submitted: false,
        loading: {
            sendVerificationCode: {} as Record<string, boolean>,
        },
    })

    const errors = {
        username() {
            if (state.username) {
                if (state.username.replaceAll(new RegExp('^\\s+', 'g'), '').length !== state.username.length) {
                    return <FormattedMessage id="ThereShouldBeNoSpacesAtTheBeginningOfTheUsername" defaultMessage="There should be no spaces at the beginning of the username" />
                }
                if (state.username.replaceAll(new RegExp('\\s+$', 'g'), '').length !== state.username.length) {
                    return <FormattedMessage id="TheUsernameCannotHaveASpaceAtTheEnd" defaultMessage="The username cannot have a space at the end" />
                }
            }
            if (!state.submitted) {
                return false;
            }
            if (!state.username) {
                return <FormattedMessage id="PleaseFillInUsername" defaultMessage="Please fill in username" />
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
        email(emailInfo: UserEmailModel) {
            if (!state.submitted) {
                return false;
            }
            if (!emailInfo.email) {
                return <FormattedMessage id="PleaseEnterYourEmail" defaultMessage="Please enter your email" />
            }
            if (!new RegExp('^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$').test(emailInfo.email)) {
                return <FormattedMessage id="EMailFormatIsIncorrect" defaultMessage="E-mail format is incorrect" />
            }
            return false;
        },
        verificationCode(emailInfo: UserEmailModel) {
            if (!state.submitted) {
                return false;
            }
            if (!emailInfo.verificationCodeEmail.verificationCode) {
                return <FormattedMessage id="PleaseFillInTheVerificationCode" defaultMessage="Please fill in the verification code" />
            }
            return false;
        }
    };

    const signUp = useOnceSubmit(async function () {
        const userEmailList: UserEmailModel[] = state.emailList.map(s => ({
            email: s.email,
            verificationCodeEmail: s.verificationCodeEmail
        }));
        await api.Authorization.signUp(state.password, state.username, userEmailList);
    });

    function nextStep() {
        state.submitted = true;
        if (state.activeStep === 0 && errors.username()) {
            return;
        }
        if (state.activeStep === 1 && errors.password()) {
            return;
        }
        if (state.activeStep === 2 && state.emailList.some(s => errors.email(s))) {
            return;
        }
        if (state.activeStep === 2 && state.emailList.some(s => errors.verificationCode(s))) {
            return;
        }
        state.submitted = false

        if (state.activeStep < state.steps.length - 1) {
            state.activeStep++;
        }
    }

    function previousActiveStep() {
        if (state.activeStep > 0) {
            state.activeStep--;
        }
    }

    function addEmailToEmailList() {
        state.emailList.push({
            id: v7(),
            email: '',
            verificationCodeEmail: {
                id: '',
                verificationCode: '',
                verificationCodeLength: 6,
            }
        });
    }

    async function sendVerificationCode(userEmailModel: UserEmailModel) {
        try {
            state.submitted = true;
            if (errors.email(userEmailModel)) {
                return;
            }
            state.submitted = false;
            state.loading.sendVerificationCode[userEmailModel.id!] = true;
            const data = await api.Authorization.sendVerificationCode(userEmailModel.email);
            userEmailModel.verificationCodeEmail.id = data.id;
            userEmailModel.verificationCodeEmail.verificationCode = data.verificationCode;
            userEmailModel.verificationCodeEmail.verificationCodeLength = data.verificationCodeLength;
        } catch (e) {
            MessageService.error(e);
        } finally {
            state.loading.sendVerificationCode[userEmailModel.id!] = false;
        }
    }

    return <div className={container}>
        <div className="flex flex-col flex-auto w-full">
            <div className="flex flex-col flex-auto w-full">
                <div className="flex justify-center" style={{ marginTop: "1em" }}>
                    <FormattedMessage id="SignUp" defaultMessage="SignUp" />
                </div>
                <Divider style={{ marginTop: "1em" }} />
                {state.activeStep === 0 && <div className="flex flex-col" style={{ marginTop: "1em" }}>
                    <div>
                        <FormattedMessage id="UsernameYouCanModifyYourUsernameAtAnyTime" defaultMessage="Username, you can modify your username at any time" />
                    </div>
                    <TextField
                        label={<FormattedMessage id="Username" defaultMessage="Username" />}
                        variant="outlined"
                        onChange={(e) => {
                            state.username = e.target.value;
                        }}
                        value={state.username}
                        autoComplete="off"
                        error={!!errors.username()}
                        helperText={errors.username()}
                        style={{ marginTop: "1em" }}
                        onKeyDown={(e) => {
                            if (!e.shiftKey && e.key === "Enter") {
                                e.preventDefault()
                                nextStep();
                            }
                        }}
                        autoFocus={true}
                    />
                </div>}
                {state.activeStep !== 0 && (<div className="flex flex-row" style={{ marginTop: "1em" }}>
                    <div style={{ marginRight: "1em" }}>
                        <FormattedMessage id="Username" defaultMessage="Username" />
                        {":"}
                    </div>
                    <div>
                        {state.username}
                    </div>
                </div>)}
                {state.activeStep > 0 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
                {state.activeStep > 1 && <div className="flex flex-row">
                    <div className="flex flex-row" style={{ marginRight: "1em" }}>
                        <FormattedMessage id="Password" defaultMessage="Password" />
                        {":"}
                    </div>
                    <FormattedMessage id="PasswordSettingIsComplete" defaultMessage="Password setting is complete" />
                </div>}
                {state.activeStep === 1 && <div className="flex flex-col">
                    <Typography style={{ marginBottom: "0em" }}>
                        <FormattedMessage id="JustLikeTheTreasureMapLetUsHideThePasswordInThisWorld" defaultMessage="Just like the treasure map, let's hide the password in this world. For example, select a paragraph as a password from Shakespeare's works." />
                    </Typography>
                    <Typography style={{ marginBottom: "0em" }}>
                        <FormattedMessage id="ThePasswordSupportsAllTheCharactersOfUTF8" defaultMessage="The password supports all the characters of UTF-8. You can use any of the language content you like as the password." />
                    </Typography>
                    <Typography style={{ marginBottom: "0em" }}>
                        <FormattedMessage id="WeDoNotProvideResetPasswordSoRememberYourPassword" defaultMessage="We do not provide reset password functions. So, remember your password." />
                    </Typography>
                    <Typography style={{ marginBottom: "1em" }}>
                        <FormattedMessage id="OnlyYouWhocanKnowThePassword" defaultMessage="We can't know your chat content and file content. Only you who can know the password." />
                    </Typography>
                    <TextField
                        label={<FormattedMessage id="Password" defaultMessage="Password" />}
                        className="flex flex-auto"
                        variant="outlined"
                        onChange={(e) => {
                            state.password = e.target.value;
                        }}
                        slotProps={{
                            htmlInput: {
                                style: {
                                    resize: "vertical",
                                }
                            }
                        }}
                        value={state.password}
                        autoComplete="off"
                        multiline={true}
                        rows={6}
                        error={!!errors.password()}
                        helperText={errors.password()}
                        autoFocus={true}
                    />
                </div>}
                {state.activeStep > 1 && <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />}
                {state.activeStep > 2 && <div>
                    {state.emailList.length > 0 && <div className="flex flex-col">
                        {state.emailList.map(s => <div key={s.id} className="flex flex-row" style={{ marginBottom: "1em" }}>
                            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                                <FormattedMessage id="Email" defaultMessage="Email" />
                                {":"}
                            </div>
                            {s.email}
                        </div>)}
                    </div>}
                </div>}
                {state.activeStep === 2 && <div className="flex flex-col">
                    <div>
                        <FormattedMessage id="BindedEmailCanSkip" defaultMessage="Binded email(Can Skip)" />
                    </div>
                    {state.emailList.map((s) => <div className="flex flex-row items-start" key={s.id} style={{ marginTop: '1em' }}>
                        <TextField
                            label={<FormattedMessage id="Email" defaultMessage="Email" />}
                            variant="outlined"
                            onChange={(e) => {
                                s.email = e.target.value;
                            }}
                            value={s.email}
                            autoComplete="off"
                            className="w-full"
                            error={!!errors.email(s)}
                            helperText={errors.email(s)}
                            autoFocus={true}
                        />
                        <div style={{ height: "56px" }} className="flex items-center">
                            <Button
                                style={{
                                    marginLeft: "1em",
                                }}
                                variant="contained"
                                onClick={() => sendVerificationCode(s)}
                                startIcon={<FontAwesomeIcon icon={state.loading.sendVerificationCode[s.id!] ? faSpinner : faPaperPlane} spin={state.loading.sendVerificationCode[s.id!]} style={{ fontSize: "medium" }} />}
                            >
                                <FormattedMessage id="Send" defaultMessage="Send" />
                            </Button>
                        </div>
                        <TextField
                            label={<FormattedMessage id="VerificationCode" defaultMessage="Verification code" />}
                            variant="outlined"
                            onChange={(e) => {
                                if (e.target.value === '') {
                                    s.verificationCodeEmail.verificationCode = e.target.value;
                                } else if (e.target.value && new RegExp("^[0-9]+$").test(e.target.value)) {
                                    s.verificationCodeEmail.verificationCode = e.target.value;
                                }
                            }}
                            value={s.verificationCodeEmail.verificationCode}
                            autoComplete="off"
                            className="w-full"
                            error={!!errors.verificationCode(s)}
                            helperText={errors.verificationCode(s)}
                            style={{ marginLeft: "1em" }}
                        />
                        <div style={{ height: "56px" }} className="flex items-center">
                            <IconButton
                                aria-label="delete"
                                onClick={() => {
                                    const index = state.emailList.findIndex(m => m.id === s.id);
                                    if (index >= 0) {
                                        state.emailList.splice(index, 1)
                                    }
                                }}
                                style={{ marginLeft: "0.2em" }}
                            >
                                <FontAwesomeIcon icon={faTrash} />
                            </IconButton>
                        </div>
                    </div>)}
                    <div style={{ marginTop: "0.9em" }}>
                        <Fab
                            color="primary"
                            aria-label="add"
                            size="small"
                            onClick={addEmailToEmailList}
                        >
                            <FontAwesomeIcon icon={faPlus} style={{ fontSize: "large" }} />
                        </Fab>
                    </div>
                </div>}
            </div>

            <Divider style={{ marginTop: "1em", marginBottom: "1em" }} />
            <div style={{ marginTop: "1em" }} className="flex flex-col">
                <Box sx={{ width: '100%' }}>
                    <Stepper activeStep={state.activeStep} alternativeLabel>
                        {state.steps.map((step) => (
                            <Step key={step.id}>
                                <StepLabel>{step.text}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                </Box>
                <div className="flex justify-between" style={{ marginTop: "1em", marginBottom: "1em" }}>
                    {state.activeStep === 0 && <div></div>}
                    {state.activeStep > 0 && <Button
                        variant="contained"
                        startIcon={<FontAwesomeIcon icon={faFloppyDisk} />}
                        onClick={previousActiveStep}
                    >
                        <FormattedMessage id="Previous" defaultMessage="Previous" />
                    </Button>}
                    {state.activeStep < state.steps.length - 1 && <Button
                        variant="contained"
                        startIcon={<FontAwesomeIcon icon={faFloppyDisk} />}
                        onClick={nextStep}
                    >
                        <FormattedMessage id="Next" defaultMessage="Next" />
                    </Button>}
                    {state.activeStep >= state.steps.length - 1 && <Button
                        variant="contained"
                        startIcon={<FontAwesomeIcon icon={signUp.loading ? faSpinner : faFloppyDisk} spin={signUp.loading} />}
                        onClick={signUp.resubmit}
                    >
                        <FormattedMessage id="SignUp" defaultMessage="SignUp" />
                    </Button>}
                </div>
            </div>
            <div>
            </div>
        </div>
    </div>
})