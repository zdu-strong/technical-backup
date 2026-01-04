export default {
    HomeButton: () => cy.xpath(`//button[contains(., 'Home')]`),
    QuestionButton: () => cy.xpath(`//button[@id='question']`),
    AnswerNameButton: () => cy.xpath(`//button[@id='answer']`),
}