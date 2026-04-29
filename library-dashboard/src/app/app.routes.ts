import { Routes } from '@angular/router';
import { PageNotFound } from './page-not-found/page-not-found';
import { Login } from './login/login';
import { Register } from './register/register';

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
