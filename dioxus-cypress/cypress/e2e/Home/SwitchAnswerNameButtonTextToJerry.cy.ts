import page from '@/page'

it('', () => {
    page.Home.AnswerNameButton().click()
    page.Home.AnswerNameButton().should("have.text", "Jerry")
})

before(() => {
    cy.visit("/")
})