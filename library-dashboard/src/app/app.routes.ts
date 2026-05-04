import { Routes } from '@angular/router';
import { PageNotFound } from './components/page-not-found/page-not-found';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { ROUTES } from './constants/api.constants';
import { ResetPassword } from './components/reset-password/reset-password';
import { Dashboard } from './components/dashboard/dashboard.component';
import { RegisterMember } from './components/register-member/register-member';

export const routes: Routes = [
    {
        path: '', component: Login, pathMatch: 'full'
    },
    {
        path: ROUTES.REGISTER.slice(1), component: Register
    },
    {
        path: ROUTES.DASHBOARD.slice(1), component: Dashboard
    },
    {
        path: ROUTES.LOGIN.slice(1), component: Login
    },
    {
        path: ROUTES.RESET_PASSWORD.slice(1), component: ResetPassword
    },
    {
        path: ROUTES.REGISTER_MEMEBER.slice(1), component: RegisterMember
    },
    {
        path: '**', redirectTo: '', pathMatch: 'full'
    },

];
