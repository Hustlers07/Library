import { Routes } from '@angular/router';
import { PageNotFound } from './component/page-not-found/page-not-found';
import { Login } from './component/login/login';
import { Register } from './component/register/register';

export const routes: Routes = [
    {
        path: '', component: Login
    },
    {
        path: 'register', component: Register
    },
    {
        path: '**', component: PageNotFound
    },
    
];
