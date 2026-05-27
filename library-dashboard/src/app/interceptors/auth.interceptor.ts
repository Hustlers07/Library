import { PLATFORM_ID, Inject, Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { API_ENDPOINTS, TOKEN_KEY, ROUTES } from '../constants/api.constants';
import { environment } from '../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private router: Router
  ) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (!environment.production) {
      console.log('Intercepted request:', req.url);
      console.log('environment.apiUrl:', environment.apiUrl);           // ← add this
      console.log('URL matches?', req.url.startsWith(environment.apiUrl)); // ← add this
      console.log('Token:', localStorage.getItem(TOKEN_KEY));
    }

    // ✅ Guard against SSR — localStorage doesn't exist on server
    if (!isPlatformBrowser(this.platformId)) {
      return next.handle(req);
    }

    // Only attach token for requests to your backend API
    if (req.url.startsWith(environment.apiUrl)) {
      // Skip login and register
      if (req.url.includes(API_ENDPOINTS.LOGIN()) || req.url.includes(API_ENDPOINTS.REGISTER())) {
        return next.handle(req);
      }

      const token = localStorage.getItem(TOKEN_KEY);
      if (token) {
        req = req.clone({
          setHeaders: { Authorization: `Bearer ${token}` }
        });
      }
    }

    // Always forward the request and handle authorization failures
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (isPlatformBrowser(this.platformId) && error.status === 401) {
          console.warn('Unauthorized request detected, redirecting to login');
          localStorage.removeItem(TOKEN_KEY);
          this.router.navigate([ROUTES.LOGIN]);
        }
        return throwError(() => error);
      })
    );
  }
}
