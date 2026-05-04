import { signal } from "@angular/core";
import { ConfigService } from "../service/config-service";
import { User } from "../models/user/user-module";

// api.constants.ts
export const API_ENDPOINTS = {
  LOGIN: (config: ConfigService) => `${config.apiUrl}/api/auth/login`,
  REGISTER: (config: ConfigService) => `${config.apiUrl}/api/auth/register`,
  PROFILE: (config: ConfigService) => `${config.apiUrl}/api/users/profile`,
  CHANGE_PASSWORD: (config: ConfigService) => `${config.apiUrl}/api/auth/change-password`
};

export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  REGISTER_MEMEBER: '/register-member',
  DASHBOARD: '/dashboard',
  RESET_PASSWORD: '/reset-password',
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
  { label: 'Register', icon: 'person_add', route: ROUTES.REGISTER_MEMEBER }

  // ,
  // { label: 'Authors', icon: 'people', route: ROUTES.AUTHORS },
  // { label: 'Genres', icon: 'category', route: ROUTES.GENRES },
  // { label: 'Settings', icon: 'settings', route: ROUTES.SETTINGS },
];