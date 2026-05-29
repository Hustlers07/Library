import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { ROUTES } from './constants/api.constants';
import { ResetPassword } from './components/reset-password/reset-password';
import { Dashboard } from './components/dashbard-component/dashboard/dashboard.component';
import { Profile } from './components/profile/profile';
import { Room } from './components/room/room';
import { UserComponent as User } from './components/user/user';
import { Plan } from './components/plan/plan';
import { Coupon } from './components/coupon/coupon';
import { Booking } from './components/booking/booking';
import { Payment } from './components/payment/payment';
import { Seat } from './components/seat/seat';

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
        path: ROUTES.ROOM.slice(1), component: Room
    },
    {
        path: ROUTES.USER.slice(1), component: User
    },
    {
        path: ROUTES.PLAN.slice(1), component: Plan
    },
    {
        path: ROUTES.COUPON.slice(1), component: Coupon
    },
    {
        path: ROUTES.BOOKING.slice(1), component: Booking
    },
     {
        path: ROUTES.PAYMENT.slice(1), component: Payment
    },
    {
        path: ROUTES.SEAT.slice(1), component: Seat
    },
    {
        path: '**', redirectTo: '', pathMatch: 'full'
    },

];
