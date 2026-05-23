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
};


export const UID = 'uid';

export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  REGISTER_MEMEBER: '/register-member',
  DASHBOARD: '/dashboard',
  RESET_PASSWORD: '/reset-password',
  PROFILE:'/user/:'+UID,
  ROOM:'/room'
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
  { label: 'Password', icon: 'key', route: ROUTES.RESET_PASSWORD },
  { label: 'Register', icon: 'person_add', route: ROUTES.REGISTER_MEMEBER },
  { label: 'Room', icon: 'room_preferences', route: ROUTES.ROOM }

  // ,
  // { label: 'Authors', icon: 'people', route: ROUTES.AUTHORS },
  // { label: 'Genres', icon: 'category', route: ROUTES.GENRES },
  // { label: 'Settings', icon: 'settings', route: ROUTES.SETTINGS },
];