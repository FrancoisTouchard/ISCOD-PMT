import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { SigninComponent } from './components/signin/signin.component';
import { LandingComponent } from './components/landing/landing.component';
import { authGuard } from './guards/auth.guard';
import { ProjectComponent } from './components/project/project.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [authGuard],
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [authGuard],
  },
  {
    path: 'project/:id',
    component: ProjectComponent,
    canActivate: [authGuard],
  },
  {
    path: 'signin',
    component: SigninComponent,
    canActivate: [authGuard],
  },
];
