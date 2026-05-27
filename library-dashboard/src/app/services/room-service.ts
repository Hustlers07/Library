import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_ENDPOINTS } from '../constants/api.constants';
import { catchError, map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

export interface SeatObj {
  id: number;
  seatId: string;
  users?: any;
  pricePerHour?: number;
  createdAt?: string;
  updatedAt?: string;
  active?: boolean;
}

export interface RoomObj {
  id: number;
  status: string;
  houseNo: string;
  floor: string;
  location: string;
  description: string;
  seats?: SeatObj[];
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
        return of([] as RoomObj[]);
      })
    );
  }

  createRoom(data: Object):Observable<RoomObj>{

    return this.http.post<RoomObj>(API_ENDPOINTS.ROOM()+"/create", data);
  }

  updateRoom(roomId: number, data: Object): Observable<RoomObj> {
    return this.http.put<RoomObj>(`${API_ENDPOINTS.ROOM()}/update/${roomId}`, data);
  }

  addUserToRoom(roomId: number, username: string): Observable<any> {
    return this.http.post(`${API_ENDPOINTS.ROOM()}/add-user/${roomId}/username/${username}`, {});
  }

  addSeatsToRoom(roomId: number, seatCount: number): Observable<any> {
    return this.http.post(`${API_ENDPOINTS.SEAT()}/add`, { roomId, seatCount });
  }

  mapUserToSeat(seatId: number, username: string): Observable<any> {
    return this.http.post(`${API_ENDPOINTS.SEAT()}/add-user/${seatId}/user/${username}`, {});
  }

  /**
   * Search rooms by optional location and description
   */
  searchRooms(location?: string, description?: string): Observable<RoomObj[]> {
    let params = new HttpParams();
    if (location) params = params.set('location', location);
    if (description) params = params.set('description', description);
    return this.http.get<RoomObj[]>(`${API_ENDPOINTS.ROOM()}/search`, { params });
  }

  /**
   * Search rooms by status
   */
  searchRoomsByStatus(status: string): Observable<RoomObj[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<RoomObj[]>(`${API_ENDPOINTS.ROOM()}/search/status`, { params });
  }

  /**
   * Disable a seat by id
   */
  disableSeat(seatId: number): Observable<any> {
    return this.http.post(`${API_ENDPOINTS.SEAT()}/disable/${seatId}`, {});
  }
}

