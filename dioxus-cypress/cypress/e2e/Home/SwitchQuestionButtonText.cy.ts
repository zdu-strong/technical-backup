import page from '@/page'

it('', () => {
    page.Home.QuestionButton().click()
    page.Home.QuestionButton().should("have.text", "What's your name?")
    page.Home.QuestionButton().click()
    page.Home.QuestionButton().should("have.text", "Who are you?")
})

before(() => {
    cy.visit("/")
})