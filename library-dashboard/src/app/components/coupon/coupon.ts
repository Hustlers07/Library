import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';
import { CouponService, Coupon as CouponModel } from '../../services/coupon-service';

@Component({
  selector: 'app-coupon',
  standalone: true,
  imports: [CommonModule, MatButtonToggleModule],
  templateUrl: './coupon.html',
  styleUrls: ['./coupon.scss'],
})
export class Coupon implements OnInit {
  loading = signal(true);
  coupons = signal<CouponModel[]>([]);
  selectedAction = 'view';

  actions = [
    { key: 'View', value: 'view' },
    { key: 'Create', value: 'create' },
    { key: 'Update', value: 'update' }
  ];

  constructor(private couponService: CouponService) { }

  ngOnInit() {
    this.fetchCoupons();
  }

  fetchCoupons() {
    this.couponService.fetchCoupons().subscribe({
      next: coupons => {
        this.coupons.set(coupons);
        this.loading.set(false);
      },
      error: err => {
        console.error('Failed to load coupons', err);
        this.coupons.set([]);
        this.loading.set(false);
      }
    });
  }

  onActionChange(event: MatButtonToggleChange) {
    this.selectedAction = event.value;
  }
}

