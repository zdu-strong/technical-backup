export default {
    HomeButton: () => cy.xpath(`//button[contains(., 'Home')]`),
    QuestionButton: () => cy.xpath(`//button[@id='question']`),
    AnswerButton: () => cy.xpath(`//button[@id='answer']`),
}