import { Routes } from '@angular/router';
import { Home } from './modules/home/home';
import { LoginComponent } from './modules/auth/login.component';

export const routes: Routes = [
  { path: '', component: Home },
  {path: 'login', component: LoginComponent },
];
