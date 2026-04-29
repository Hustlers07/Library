// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { API_ENDPOINTS, TOKEN_KEY } from '../constants/api.constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  

  constructor(private http: HttpClient) {}

  login(credentials: { email: string; password: string }): Observable<string> {
    return this.http.post<{ token: string }>(API_ENDPOINTS.LOGIN, credentials).pipe(
      map(response => {
        const token = response?.token;
        if (token) {
          this.setToken(token);
        }
        return token;
      })
    );
  }

  setToken(token: string | null) {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token);
    } else {
      localStorage.removeItem(TOKEN_KEY);
    }
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  logout() {
    this.setToken(null);
    // optionally navigate or clear other state
  }
}
