import page from '@/page'

it('', () => {
    page.NotFound.ReturnToHomeButton().click()
    page.Chat.SettingButton().should('exist')
})

before(() => {
    cy.visit("/404")
})