export const API_BASE_URL = 'http://localhost:3000/api';

// You can also define specific endpoints
export const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/login`,
  REGISTER: `${API_BASE_URL}/register`,
  PROFILE: `${API_BASE_URL}/profile`
};

export const TOKEN_KEY = 'authToken';