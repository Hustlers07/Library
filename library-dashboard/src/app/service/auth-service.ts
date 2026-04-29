import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../constants/api.constants';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
 constructor(private http: HttpClient) {}

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(API_ENDPOINTS.LOGIN, credentials);
  }

  getProfile(): Observable<any> {
    return this.http.get(API_ENDPOINTS.PROFILE);
  }
}
