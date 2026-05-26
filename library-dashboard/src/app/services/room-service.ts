import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_ENDPOINTS } from '../constants/api.constants';
import { catchError, map } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

export interface RoomObj {
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

  getAllRooms(): Observable<RoomObj[]>{

    return this.http.get<RoomObj[]>(API_ENDPOINTS.ROOM()+"/all").pipe(
      map((rooms: RoomObj[]) =>  rooms.sort((a, b) => {
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

  createRoom(data: Object):Observable<RoomObj>{

    return this.http.post<RoomObj>(API_ENDPOINTS.ROOM()+"/create", data);
  }
}
