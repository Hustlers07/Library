import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL, API_ENDPOINTS } from '../constants/api.constants';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    console.log('Intercepted request:', req.url);
    // Only attach token for requests to your backend API
    if (req.url.startsWith(API_BASE_URL)) {
      // Skip login and register
      if (req.url.includes(API_ENDPOINTS.LOGIN) || req.url.includes(API_ENDPOINTS.REGISTER)) {
        return next.handle(req);
      }

      const token = localStorage.getItem('authToken');
      if (token) {
        req = req.clone({
          setHeaders: { Authorization: `Bearer ${token}` }
        });
      }
    }

    // Always forward the request
    return next.handle(req);
  }
}
