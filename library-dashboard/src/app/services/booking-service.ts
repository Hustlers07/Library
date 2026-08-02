import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { API_ENDPOINTS } from '../constants/api.constants';

export type BookingType = 'FLOOR' | 'SEAT';
export type DurationType = 'HOURLY' | 'DAILY' | 'MONTHLY';

export interface BookingPayload {
  userName: string;
  roomId: number;
  bookingType: BookingType;
  durationType: DurationType;
  duration: number;
  specialNotes?: string;
  couponCode?: string;
  planName?: string;
  seatIds?: number[];
}

export interface BookingObj {
  id: number;
  userId: number;
  roomId: number;
  bookingType: BookingType;
  durationType: DurationType;
  duration: number;
  startTime: string;
  endTime: string;
  status: string;
  basePrice: number;
  totalPrice: number;
  specialNotes?: string;
  isRenewal?: boolean;
  renewalCount?: number;
  parentBookingId?: number | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface RenewBookingPayload {
  bookingId: number;
  newStartTime: string;
  renewalCount?: number;
  renewalNotes?: string;
}

export interface RenewBookingResponse {
  originalBookingId: number;
  renewalBookings: BookingObj[];
  totalRenewalsCreated: number;
  message?: string;
}

@Injectable({ providedIn: 'root' })
export class BookingService {
  constructor(private http: HttpClient) {}

  fetchBookings(): Observable<BookingObj[]> {
    return this.http.get<BookingObj | BookingObj[]>(API_ENDPOINTS.BOOKINGS()).pipe(
      map((res) => Array.isArray(res) ? res : [res])
    );
  }

  createBooking(payload: BookingPayload): Observable<BookingObj> {
    return this.http.post<BookingObj>(API_ENDPOINTS.BOOKINGS(), payload);
  }

  fetchBookingById(id: number): Observable<BookingObj> {
    return this.http.get<BookingObj>(API_ENDPOINTS.BOOKING_BY_ID(id));
  }

  renewBooking(id: number, payload: RenewBookingPayload): Observable<RenewBookingResponse> {
    return this.http.post<RenewBookingResponse>(API_ENDPOINTS.BOOKING_RENEW(id), payload);
  }

  cancelBooking(id: number): Observable<any> {
    return this.http.post<any>(API_ENDPOINTS.BOOKING_CANCEL(id), {});
  }
}
