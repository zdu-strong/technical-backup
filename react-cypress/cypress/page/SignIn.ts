export default {
    username: () => cy.xpath("//legend/span[.='Account ID']/../../..//input", { timeout: 180000 }),
    password: () => cy.xpath(`//label[.='Password']/../div/input`),
    signInButton: () => cy.xpath(`//button[.='Sign In']`),
    incorrectPasswordOrUsernameDialog: () => cy.xpath(`//div[contains(@class, "MuiAlert-message")][contains(., "Incorrect username or password")]`, { timeout: 180000 }),
}