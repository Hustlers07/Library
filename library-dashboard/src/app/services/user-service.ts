import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user/user-module';
import { API_ENDPOINTS } from '../constants/api.constants';
import { catchError, map, Observable, of, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) { }

  getUserDetails(uid: string) {
    return this.http.get(API_ENDPOINTS.USERS() + '/' + uid).pipe(
      map(profile => new User(profile)),
      catchError(error => {
        console.error('Error fetching user details:', error);
        return throwError(() => new Error('Failed to load user details'));
      })
    );
  }

  fetchUsers(): Observable<User[]> {
    return this.http.get<any[]>(API_ENDPOINTS.USERS()).pipe(
      map((users: any[]) => users.map(user => new User(user))),
      catchError(error => {
        console.error('Error fetching users:', error);
        return of([]);
      })
    );
  }

  registerMember(payload: {
    email: string;
    password: string;
    confirmPassword: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
  }): Observable<any> {
    return this.http.post<any>(API_ENDPOINTS.REGISTER(), payload).pipe(
      catchError(error => {
        console.error('Error registering member:', error);
        return throwError(() => new Error('Failed to register member'));
      })
    );
  }

  updateUser(id: number, payload: Partial<{
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    isActive: boolean;
  }>): Observable<User> {
    return this.http.put(API_ENDPOINTS.USERS() + '/' + id, payload).pipe(
      map(profile => new User(profile)),
      catchError(error => {
        console.error('Error updating user:', error);
        return throwError(() => new Error('Failed to update user'));
      })
    );
  }

  setUserActive(id: number, isActive: boolean): Observable<User> {
    return this.http.patch(API_ENDPOINTS.USERS() + '/' + id, { isActive }).pipe(
      map(profile => new User(profile)),
      catchError(error => {
        console.error('Error updating user status:', error);
        return throwError(() => new Error('Failed to update user status'));
      })
    );
  }

  changePassword(data: { currentPassword: string; newPassword: string }): Observable<string> {
    return this.http.post<{ message: string }>(API_ENDPOINTS.CHANGE_PASSWORD(), data).pipe(
      map(response => response?.message),
      catchError(error => {
        console.error('Error changing password:', error);
        return throwError(() => new Error('Failed to change password'));
      })
    );
  }
}
