import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_ENDPOINTS, TOKEN_KEY } from '../constants/api.constants';
import { ConfigService } from '../service/config-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private config:ConfigService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    console.log('Intercepted request:', req.url);
    // Only attach token for requests to your backend API
    if (req.url.startsWith(this.config.apiUrl)) {
      // Skip login and register
      if (req.url.includes(API_ENDPOINTS.LOGIN(this.config)) || req.url.includes(API_ENDPOINTS.REGISTER(this.config))) {
        return next.handle(req);
      }

      const token = localStorage.getItem(TOKEN_KEY);
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
