import { existsWindow } from '@common/exists-window/exists-window';

let ClientAddress = 'http://127.0.0.1:3000';

if (existsWindow) {
  const origin = window.location.origin;
  if (origin.startsWith("http")) {
    ClientAddress = origin;
  }
}

export { ClientAddress }