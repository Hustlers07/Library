import { ConfigService } from "../service/config-service";

// api.constants.ts
export const API_ENDPOINTS = {
  LOGIN: (config: ConfigService) => `${config.apiUrl}/login`,
  REGISTER: (config: ConfigService) => `${config.apiUrl}/register`,
  PROFILE: (config: ConfigService) => `${config.apiUrl}/profile`
};


export const TOKEN_KEY = 'authToken';