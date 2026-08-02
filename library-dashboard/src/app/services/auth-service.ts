// src/app/services/auth.service.ts
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, shareReplay } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { activeUser, API_ENDPOINTS, ROUTES, TOKEN_KEY } from '../constants/api.constants';
import { User } from '../models/user/user-module';
import { isPlatformBrowser } from '@angular/common';
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private profileCache$: Observable<User> | null = null;
  private profileCacheTime: number | null = null;
  private readonly CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient,
  ) { }

  private extractToken(response: any): string | null {
    if (!response) {
      return null;
    }
    if (typeof response.token === 'string' && response.token) {
      return response.token;
    }
    if (typeof response.access_token === 'string' && response.access_token) {
      return response.access_token;
    }
    if (typeof response.authToken === 'string' && response.authToken) {
      return response.authToken;
    }
    if (typeof response.data?.token === 'string' && response.data.token) {
      return response.data.token;
    }
    return null;
  }

  login(credentials: { email: string; password: string }): Observable<string | null> {
    return this.http.post<any>(API_ENDPOINTS.LOGIN(), credentials).pipe(
      map(response => {
        const token = this.extractToken(response);
        if (token) {
          this.setToken(token);
        }
        return token;
      })
    );
  }

  register(credentials: Object): Observable<string | null> {
    return this.http.post<any>(API_ENDPOINTS.REGISTER(), credentials).pipe(
      map(response => {
        const token = this.extractToken(response);
        if (token) {
          this.setToken(token);
        }
        return token;
      })
    );
  }

  changePassword(data: Object): Observable<string> {
    return this.http.post<{ message: string }>(API_ENDPOINTS.CHANGE_PASSWORD(), data).pipe(
      map(response => response?.message)
    );
  }

  // fetchUsers(): Observable<User[]> {
  //   return this.http.get<any[]>(API_ENDPOINTS.USERS()).pipe(
  //     map(
  //       (users: any[]) => users.map(user => new User(user))
  //     )
  //   );
  // }

  getProfile(): Observable<User> {
    return this.http.get(API_ENDPOINTS.PROFILE()).pipe(map(profile => new User(profile)));
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

  getCurrentUsername(): string {
    const token = this.getToken();
    if (!token) return '';

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub ?? ''; // ← 'sub' is the username in your JWT
    } catch {
      return '';
    }
  }

  getCachedProfile(): Observable<User> {
    const now = Date.now();
    const isExpired = !this.profileCacheTime || (now - this.profileCacheTime) > this.CACHE_TTL_MS;

    if (!this.profileCache$ || isExpired) {
      this.profileCacheTime = now;
      this.profileCache$ = this.getProfile().pipe(
        shareReplay(1)
      );
    }
    return this.profileCache$;
  }

  logout() {

    if (this.getToken() != undefined && this.getToken() != null) {
      this.setToken(null);
      activeUser.set(null);
      this.profileCache$ = null;
      this.profileCacheTime = null;
    }
  }

}
