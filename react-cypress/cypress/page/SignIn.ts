export default {
    username: () => cy.xpath(`//input[@name="username"]`, { timeout: 180000 }),
    password: () => cy.xpath(`//input[@name="password"]`),
    signInButton: () => cy.xpath(`//button[.='Sign In']`),
    incorrectPasswordOrUsernameDialog: () => cy.xpath(`//div[contains(@class, "MuiAlert-message")][contains(., "Incorrect username or password")]`, { timeout: 180000 }),
}