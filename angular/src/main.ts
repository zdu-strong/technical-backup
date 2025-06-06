import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './common/config/app.config';
import { Home } from './app/home/home/home';

bootstrapApplication(Home, appConfig)
  .catch((err) => console.error(err));
