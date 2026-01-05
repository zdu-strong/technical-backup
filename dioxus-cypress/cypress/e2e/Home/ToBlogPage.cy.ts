import page from '@/page'

it('', () => {
    page.Home.BlogButton().click()
    page.Blog.BlogTitile().should("have.text", "This is blog #1!")
})

before(() => {
    cy.visit("/")
})