import { ConfigService } from "../service/config-service";

// api.constants.ts
export const API_ENDPOINTS = {
  LOGIN: (config: ConfigService) => `${config.apiUrl}/api/auth/login`,
  REGISTER: (config: ConfigService) => `${config.apiUrl}/api/auth/register`,
  PROFILE: (config: ConfigService) => `${config.apiUrl}/api/auth/profile`
};


export const TOKEN_KEY = 'authToken';