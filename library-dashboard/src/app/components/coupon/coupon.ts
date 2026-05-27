import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CouponService, Coupon as CouponModel, CouponCreatePayload } from '../../services/coupon-service';

@Component({
  selector: 'app-coupon',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonToggleModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './coupon.html',
  styleUrls: ['./coupon.scss'],
})
export class Coupon implements OnInit {
  loading = signal(true);
  coupons = signal<CouponModel[]>([]);
  selectedAction = 'view';
  isSubmitting = signal(false);
  submissionMessage = signal<string | null>(null);

  couponForm = null as any;

  actions = [
    { key: 'View', value: 'view' },
    { key: 'Create', value: 'create' },
    { key: 'Update', value: 'update' }
  ];

  constructor(private couponService: CouponService, private fb: FormBuilder) {
    const datePattern = /^\d{4}-\d{2}-\d{2}$/;

    this.couponForm = this.fb.group({
      couponCode: ['', Validators.required],
      description: ['', Validators.required],
      discountPercentage: [0, [Validators.required, Validators.min(0)]],
      discountAmount: [0, [Validators.required, Validators.min(0)]],
      minimumBookingAmount: [0, [Validators.required, Validators.min(0)]],
      maximumDiscountAmount: [0, [Validators.required, Validators.min(0)]],
      usageLimit: [1, [Validators.required, Validators.min(1)]],
      validFrom: ['', [Validators.required, Validators.pattern(datePattern)]],
      validTill: ['', [Validators.required, Validators.pattern(datePattern)]],
    }, { validators: [this.validDateRangeValidator] });
  }

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
    this.submissionMessage.set(null);
  }

  private buildIstIso(dateString: string, endOfDay = false): string {
    const [year, month, day] = dateString.split('-').map(Number);
    const istOffsetMinutes = 5 * 60 + 30;
    const hour = endOfDay ? 23 : 0;
    const minute = endOfDay ? 59 : 0;
    const second = endOfDay ? 59 : 0;
    const millisecond = endOfDay ? 999 : 0;
    const utcMillis = Date.UTC(year, month - 1, day, hour, minute, second, millisecond) - istOffsetMinutes * 60000;
    return new Date(utcMillis).toISOString();
  }

  private validDateRangeValidator = (group: any) => {
    const validFrom = group.get('validFrom')?.value;
    const validTill = group.get('validTill')?.value;

    if (!validFrom || !validTill) {
      return null;
    }

    return validTill <= validFrom ? { validTillBeforeValidFrom: true } : null;
  };

  onCreateCoupon() {
    if (this.couponForm.invalid) {
      this.couponForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.submissionMessage.set(null);

    const raw = this.couponForm.value;
    const payload: CouponCreatePayload = {
      couponCode: raw.couponCode,
      description: raw.description,
      discountPercentage: Number(raw.discountPercentage),
      discountAmount: Number(raw.discountAmount),
      minimumBookingAmount: Number(raw.minimumBookingAmount),
      maximumDiscountAmount: Number(raw.maximumDiscountAmount),
      usageLimit: Number(raw.usageLimit),
      validFrom: this.buildIstIso(raw.validFrom),
      validTill: this.buildIstIso(raw.validTill, true),
    };

    this.couponService.createCoupon(payload).subscribe({
      next: created => {
        const current = this.coupons();
        this.coupons.set([created, ...current]);
        this.submissionMessage.set('Coupon created successfully.');
        this.couponForm.reset({
          couponCode: '',
          description: '',
          discountPercentage: 0,
          discountAmount: 0,
          minimumBookingAmount: 0,
          maximumDiscountAmount: 0,
          usageLimit: 1,
          validFrom: '',
          validTill: ''
        });
        this.selectedAction = 'view';
      },
      error: err => {
        console.error('Failed to create coupon', err);
        this.submissionMessage.set('Failed to create coupon. Please check the values and try again.');
      },
      complete: () => {
        this.isSubmitting.set(false);
      }
    });
  }
}

