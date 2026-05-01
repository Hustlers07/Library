import { Routes } from '@angular/router';
import { PageNotFound } from './component/page-not-found/page-not-found';
import { Login } from './component/login/login';
import { Register } from './component/register/register';
import { ROUTES } from './constants/api.constants';
import { Dashboard } from './component/dashboard/dashboard';

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
        path: '**', redirectTo: '', pathMatch: 'full'
    },

];
