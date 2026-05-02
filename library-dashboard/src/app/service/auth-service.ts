// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { activeUser, API_ENDPOINTS, ROUTES, TOKEN_KEY } from '../constants/api.constants';
import { ConfigService } from './config-service';
import { User } from '../models/user/user-module';
@Injectable({
  providedIn: 'root'
})
export class AuthService {


  constructor(private http: HttpClient, 
    private config: ConfigService,

  ) { }

  login(credentials: { email: string; password: string }): Observable<string> {
    return this.http.post<{ token: string }>(API_ENDPOINTS.LOGIN(this.config), credentials).pipe(
      map(response => {
        const token = response?.token;
        if (token) {
          this.setToken(token);
        }
        return token;
      })
    );
  }

  register(credentials: Object): Observable<string> {
    return this.http.post<{ token: string }>(API_ENDPOINTS.REGISTER(this.config), credentials).pipe(
      map(response => {
        const token = response?.token;
        if (token) {
          this.setToken(token);
        }
        return token;
      })
    );
  }

  getProfile(): Observable<User> {
    return this.http.get(API_ENDPOINTS.PROFILE(this.config)).pipe(map(profile => new User(profile)));
  }

  setToken(token: string | null) {

    console.log('Setting token:', token);
    if (typeof window !== 'undefined' && window.localStorage) {
      if (token) {
        localStorage.setItem(TOKEN_KEY, token);
      } else {
        localStorage.removeItem(TOKEN_KEY);
      }
    } else {
      console.warn('No token found in localStorage during logout.');
    }

  }

  getToken(): string | null {
    try {
      return localStorage.getItem(TOKEN_KEY);
    } catch (error) {
      console.error('Error fetching token:', error);
      return null;
    }
    
  }

  logout() {

    if (this.getToken() != undefined && this.getToken() != null) {
      this.setToken(null);
      activeUser.set(null);
      
    }
  }

}
