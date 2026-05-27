import { PLATFORM_ID, Inject, Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { API_ENDPOINTS, ROUTES } from '../constants/api.constants';
import { environment } from '../../environments/environment';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../services/auth-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private router: Router,
    private authService: AuthService
  ) { }

  private isApiRequest(url: string): boolean {
    const apiBase = environment.apiUrl.replace(/\/+$|^$/g, '');
    return url.startsWith(apiBase) || url.includes(`/${environment.basePath.replace(/^\/|\/$/g, '')}/api/`) || url.includes('/api/');
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    if (!environment.production) {
      console.log('Intercepted request:', req.url);
      console.log('environment.apiUrl:', environment.apiUrl);
      console.log('isApiRequest:', this.isApiRequest(req.url));
      console.log('Token:', this.authService.getToken());
    }

    if (!isPlatformBrowser(this.platformId)) {
      return next.handle(req);
    }

    if (this.isApiRequest(req.url)) {
      const isAuthCall = req.url.includes('/api/auth/login') || req.url.includes('/api/auth/register');
      if (!isAuthCall) {
        const token = this.authService.getToken();
        if (token) {
          req = req.clone({
            setHeaders: { Authorization: `Bearer ${token}` }
          });
        }
      }
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (isPlatformBrowser(this.platformId) && error.status === 401) {
          console.warn('Unauthorized request detected, redirecting to login');
          this.authService.logout();
          this.router.navigate([ROUTES.LOGIN]);
        }
        return throwError(() => error);
      })
    );
  }
}
