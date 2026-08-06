import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { API_ENDPOINTS } from '../constants/api.constants';

export const PLAN_TYPES = [
  'FLOOR_BASIC',
  'FLOOR_STANDARD',
  'FLOOR_PREMIUM',
  'FLOOR_ENTERPRISE',
  'SEAT_BASIC',
  'SEAT_STANDARD',
  'SEAT_PREMIUM',
  'SEAT_ENTERPRISE',
] as const;

export type PlanType = typeof PLAN_TYPES[number];

export interface Plan {
  id: number;
  planName: string;
  planType: PlanType;
  description: string;
  price: number;
  validityDays: number;
  hourlyLimit: number;
  dailyLimit: number;
  monthlyLimit: number;
  floorAccessAllowed: boolean;
  seatAccessAllowed: boolean;
  isActive: boolean;
  discountPercentage: number;
  createdAt: string;
  updatedAt: string;
}

export interface PlanCreatePayload {
  planName: string;
  planType: PlanType;
  description: string;
  price: number;
  validityDays: number;
  hourlyLimit: number;
  dailyLimit: number;
  monthlyLimit: number;
  floorAccessAllowed: boolean;
  seatAccessAllowed: boolean;
  discountPercentage: number;
}

@Injectable({
  providedIn: 'root'
})
export class PlanService {
  constructor(private http: HttpClient) { }

  fetchPlans(): Observable<Plan[]> {
    return this.http.get<Plan | Plan[]>(API_ENDPOINTS.PLANS()).pipe(
      map((response) => Array.isArray(response) ? response : [response])
    );
  }

  createPlan(payload: PlanCreatePayload): Observable<Plan> {
    return this.http.post<Plan>(API_ENDPOINTS.PLANS(), payload);
  }

  updatePlan(id: number, payload: PlanCreatePayload): Observable<Plan> {
    return this.http.put<Plan>(API_ENDPOINTS.PLAN_BY_ID(id), payload);
  }
}
