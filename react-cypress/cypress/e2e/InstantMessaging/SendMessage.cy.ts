import page from '@/page'
import { v4 } from 'uuid'
import * as action from '@/action'

it('', () => {
    page.Chat.MessageContentInput().type(message).type("{enter}")
    page.Chat.Message(message).should("exist")
})

before(() => {
    action.signUp(email, password)
    cy.visit("/chat")
})

const message = `Hello, World! ${v4()}`
const email = `${v4()}zdu.strong@gmail.com`
const password = 'Hello, World!'