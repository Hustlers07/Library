import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { ROUTES } from './constants/api.constants';
import { ResetPassword } from './components/reset-password/reset-password';
import { Dashboard } from './components/dashboard/dashboard.component';
import { Profile } from './components/profile/profile';

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
        path: ROUTES.PROFILE.slice(1), component: Profile
    },
    {
        path: '**', redirectTo: '', pathMatch: 'full'
    },

];
