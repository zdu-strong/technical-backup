import page from '@/page'

it('', () => {
    page.Blog.NextButton().click()
    page.Blog.BlogTitile().should("have.text", "This is blog #2!")
    cy.url().should("include", "/blog/2")
})

before(() => {
    cy.visit("/blog/1")
})