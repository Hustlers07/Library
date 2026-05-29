import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { API_ENDPOINTS } from '../constants/api.constants';

export interface Coupon {
  id: number;
  couponCode: string;
  description: string;
  discountPercentage: number;
  discountAmount: number;
  minimumBookingAmount: number;
  maximumDiscountAmount: number;
  usageLimit: number;
  usedCount: number;
  validFrom: string;
  validTill: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CouponCreatePayload {
  couponCode: string;
  description: string;
  discountPercentage: number;
  discountAmount: number;
  minimumBookingAmount: number;
  maximumDiscountAmount: number;
  usageLimit: number;
  validFrom: string;
  validTill: string;
}

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  constructor(private http: HttpClient) { }

  fetchCoupons(): Observable<Coupon[]> {
    return this.http.get<Coupon | Coupon[]>(API_ENDPOINTS.COUPON()).pipe(
      map((response) => Array.isArray(response) ? response : [response])
    );
  }

  createCoupon(payload: CouponCreatePayload): Observable<Coupon> {
    return this.http.post<Coupon>(API_ENDPOINTS.COUPON(), payload);
  }

  fetchCouponByCode(code: string): Observable<Coupon> {
    return this.http.get<Coupon>(API_ENDPOINTS.COUPON_BY_CODE(code));
  }

  fetchCouponById(id: number): Observable<Coupon> {
    return this.http.get<Coupon>(API_ENDPOINTS.COUPON_BY_ID(id));
  }

  updateCoupon(id: number, payload: CouponCreatePayload): Observable<Coupon> {
    return this.http.put<Coupon>(API_ENDPOINTS.COUPON_BY_ID(id), payload);
  }
}
