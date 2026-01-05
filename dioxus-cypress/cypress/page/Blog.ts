export default {
    HomeButton: () => cy.xpath(`//button[contains(., 'Home')]`),
    BlogButton: () => cy.xpath(`//button[contains(., 'Blog')]`),
    PreviousButton: () => cy.xpath(`//button[contains(., 'Previous')]`),
    NextButton: () => cy.xpath(`//button[contains(., 'Next')]`),
    BlogTitile: () => cy.xpath(`//div[@id='blog']/h1`),
}