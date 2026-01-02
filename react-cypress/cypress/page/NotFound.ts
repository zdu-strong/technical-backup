export default {
    NotFoundText: () => cy.xpath(`//div[contains(@class, 'MuiPaper')]/div[contains(., 'Not Found')]`),
    ReturnToHomeButton: () => cy.xpath(`//a[contains(., 'To home')]`),
}