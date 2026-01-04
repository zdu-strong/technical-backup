import page from '@/page'

it('', () => {
    page.Home.AnswerNameButton().click()
    page.Home.AnswerNameButton().should("have.text", "Jerry")
    page.Home.AnswerNameButton().click()
    page.Home.AnswerNameButton().should("have.text", "Tom")
})

before(() => {
    cy.visit("/")
})