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
}
