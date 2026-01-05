import page from '@/page'

it('', () => {
    page.Home.QuestionButton().click()
    page.Home.QuestionButton().should("have.text", "What's your name?")
})

before(() => {
    cy.visit("/")
})