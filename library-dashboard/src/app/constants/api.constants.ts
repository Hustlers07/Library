import { signal } from "@angular/core";
import { User } from "../models/user/user-module";
import { environment } from '../../environments/environment';


// api.constants.ts
export const API_ENDPOINTS = {
  LOGIN: () => `${environment.apiUrl}/${environment.basePath}/api/auth/login`,
  REGISTER: () => `${environment.apiUrl}/${environment.basePath}/api/auth/register`,
  PROFILE: () => `${environment.apiUrl}/${environment.basePath}/api/users/profile`,
  CHANGE_PASSWORD: () => `${environment.apiUrl}/${environment.basePath}/api/auth/change-password`,
  USERS: () => `${environment.apiUrl}/${environment.basePath}/api/users`,
  ROOM: () => `${environment.apiUrl}/${environment.basePath}/api/room`,
  SEAT: () => `${environment.apiUrl}/${environment.basePath}/api/seat`,
  COUPON: () => `${environment.apiUrl}/${environment.basePath}/api/coupons`,
};


export const UID = 'uid';

export const ROUTES = {
  USER: '/user',
  LOGIN: '/login',
  REGISTER: '/register',
  REGISTER_MEMEBER: '/register-member',
  DASHBOARD: '/dashboard',
  RESET_PASSWORD: '/reset-password',
  PROFILE:'/user/:'+UID,
  ROOM:'/room',
  PLAN:'/plan',
  COUPON:'/coupon',
  BOOKING:'/booking',
  PAYMENT:'/payment',
  SEAT:'/seat',
};



export const TOKEN_KEY = 'authToken';

export const progressLoading = signal(false);

export const activeUser = signal<User | null>(null);

export type MenuItem = {
  label: string;
  icon: string;
  route: string;
};

export const MENU_ITEMS: MenuItem[] = [
  { label: 'Dashboard', icon: 'dashboard', route: ROUTES.DASHBOARD },
  { label: 'Accounts', icon: 'people', route: ROUTES.USER },
    { label: 'Seats', icon: 'event_seat', route: ROUTES.SEAT },
  { label: 'Rooms', icon: 'room_preferences', route: ROUTES.ROOM },
  { label: 'Payments', icon: 'currency_rupee', route: ROUTES.PAYMENT },
  { label: 'Plans', icon: 'sell', route: ROUTES.PLAN },
  { label: 'Coupons', icon: 'local_activity', route: ROUTES.COUPON },
  { label: 'Bookings', icon: 'event', route: ROUTES.BOOKING },
  


  // ,
  // { label: 'Authors', icon: 'people', route: ROUTES.AUTHORS },
  // { label: 'Genres', icon: 'category', route: ROUTES.GENRES },
  // { label: 'Settings', icon: 'settings', route: ROUTES.SETTINGS },
];