import page from '@/page'

it('', () => {
    page.Blog.PreviousButton().click()
    page.Blog.BlogTitile().should("have.text", "This is blog #0!")
    cy.url().should("include", "/blog/0")
})

before(() => {
    cy.visit("/blog/1")
})