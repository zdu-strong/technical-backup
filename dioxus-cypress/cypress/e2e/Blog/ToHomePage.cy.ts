import page from '@/page'

it('', () => {
    page.Blog.HomeButton().click()
    page.Home.AnswerNameInput().should("have.value", "Tom")
})

before(() => {
    cy.visit("/blog/1")
})