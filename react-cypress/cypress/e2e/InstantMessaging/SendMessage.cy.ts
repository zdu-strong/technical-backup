import page from '../../page'
import { v7 } from 'uuid'
import * as action from '../../action'

it('', () => {
  page.Chat.MessageContentInput().type(message).type("{enter}")
  page.Chat.Message(message).should("exist")
})

before(() => {
  cy.visit("/sign-up")
  action.signUp(email, password)
})

const message = `Hello, World! ${v7()}`
const email = `${v7()}zdu.strong@gmail.com`
const password = 'Hello, World!'