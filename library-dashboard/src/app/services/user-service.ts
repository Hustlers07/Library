import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user/user-module';
import { API_ENDPOINTS } from '../constants/api.constants';
import { ConfigService } from './config-service';
import { catchError, map, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient, private config: ConfigService) { }

  getUserDetails(uid: string) {
    return this.http.get(API_ENDPOINTS.USERS(this.config) + "/" + uid).pipe(
      map(profile => new User(profile)),
      catchError(error => {
        console.error('Error fetching user details:', error);
        // Optionally transform the error into something user-friendly
        return throwError(() => new Error('Failed to load user details'));
      })
    );
  }
}
