export default {
    username: () => cy.xpath(`//input[@name="username"]`),
    nextStepButton: () => cy.xpath("//button[.='Next']"),
    password: () => cy.xpath(`//input[@name="password"]`),
    addEmailOrPhoneNumber: () => cy.xpath(`//button[@aria-label='add']`),
    email: () => cy.xpath(`//label[.='Email']/../div/input`),
    sendVerificationCodeButton: () => cy.xpath(`//button[.='Send']`),
    verificationCodeInput: () => cy.xpath(`//label[.='Verification code']/../div/input`),
    signUpButton: () => cy.xpath(`//button[.='Sign Up']`),
    incorrectVerificationCodeDialog: () => cy.xpath(`//div[contains(@class, "MuiAlert-message")][contains(., "The verification code is wrong!")]`, { timeout: 180000 }),
}