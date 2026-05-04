import { Routes } from '@angular/router';
import { PageNotFound } from './component/page-not-found/page-not-found';
import { Login } from './component/login/login';
import { Register } from './component/register/register';
import { ROUTES } from './constants/api.constants';
import { ResetPassword } from './component/reset-password/reset-password';
import { Dashboard } from './component/dashboard/dashboard.component';
import { RegisterMember } from './component/register-member/register-member';

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
