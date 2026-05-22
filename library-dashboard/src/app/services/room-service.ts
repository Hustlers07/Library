import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_ENDPOINTS } from '../constants/api.constants';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';
import { error } from 'console';

export interface Room {
  id: number,
  status: string,
  houseNo: string,
  floor: string,
  location: string,
  description: string
}

@Injectable({
  providedIn: 'root',
})
export class RoomService {

  constructor(private http: HttpClient){}

  getAllRooms(): Observable<Room[]>{

    return this.http.get<Room[]>(API_ENDPOINTS.ROOM()+"/all").pipe(
      map((rooms: Room[]) =>  rooms.sort((a, b) => {
        const aOccupied = a.status.split('_')[1] === 'OCCUPIED';
        const bOccupied = b.status.split('_')[1] === 'OCCUPIED';
        return +aOccupied - +bOccupied; // available first
      })),
      catchError(error => {
        console.error('Error while fetching all rooms, error: ', error);
        return throwError(()=> new Error('Failed to load rooms details.'));
      })
    );
  }
}
