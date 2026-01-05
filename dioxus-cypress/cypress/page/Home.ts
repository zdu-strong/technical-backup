export default {
    HomeButton: () => cy.xpath(`//button[contains(., 'Home')]`),
    BlogButton: () => cy.xpath(`//button[contains(., 'Blog')]`),
    QuestionButton: () => cy.xpath(`//button[@id='question']`),
    AnswerNameButton: () => cy.xpath(`//button[@id='answer']`),
    AnswerNameInput: () => cy.xpath(`//input[@name='name']`),
}