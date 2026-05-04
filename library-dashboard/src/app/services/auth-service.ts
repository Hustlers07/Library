// src/app/services/auth.service.ts
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { activeUser, API_ENDPOINTS, ROUTES, TOKEN_KEY } from '../constants/api.constants';
import { ConfigService } from './config-service';
import { User } from '../models/user/user-module';
import { isPlatformBrowser } from '@angular/common';
@Injectable({
  providedIn: 'root'
})
export class AuthService {


  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient,
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

  changePassword(data: Object): Observable<string> {
    return this.http.post<{ message: string }>(API_ENDPOINTS.CHANGE_PASSWORD(this.config), data).pipe(
      map(response => response?.message)
    );
  }

  fetchUsers(): Observable<User[]> {
    return this.http.get<any[]>(API_ENDPOINTS.USERS(this.config)).pipe(
      map(
        (users: any[]) => users.map(user => new User(user))
      )
    );
  }

  getProfile(): Observable<User> {
    return this.http.get(API_ENDPOINTS.PROFILE(this.config)).pipe(map(profile => new User(profile)));
  }

  setToken(token: string | null) {
    if (isPlatformBrowser(this.platformId)) {
      if (token) {
        localStorage.setItem(TOKEN_KEY, token);
      } else {
        localStorage.removeItem(TOKEN_KEY);
      }
    }
  }

  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(TOKEN_KEY);
    }
    return null;
  }

  logout() {

    if (this.getToken() != undefined && this.getToken() != null) {
      this.setToken(null);
      activeUser.set(null);

    }
  }

}
