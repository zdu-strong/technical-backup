import page from '@/page'

it('', () => {
    page.Home.AnswerNameInput().clear().type("John Williams")
    page.Home.AnswerNameButton().should("have.text", "John Williams")
})

before(() => {
    cy.visit("/")
})