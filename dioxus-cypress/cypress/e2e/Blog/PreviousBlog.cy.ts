import page from '@/page'

it('', () => {
    page.Blog.PreviousButton().click()
    page.Blog.BlogTitile().should("have.text", "This is blog #0!")
    cy.location('pathname').should('equal', "/blog/0")
})

before(() => {
    cy.visit("/blog/1")
})