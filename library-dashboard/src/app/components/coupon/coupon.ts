import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
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
  isUpdateLoading = signal(false);
  submissionMessage = signal<string | null>(null);
  updateMessage = signal<string | null>(null);
  searchError = signal<string | null>(null);
  selectedCoupon = signal<CouponModel | null>(null);

  selectedSortField = signal<string>('couponCode');
  sortDirection = signal<'asc' | 'desc'>('asc');
  sortedCoupons = computed(() => this.sortCoupons(this.coupons()));

  couponForm = null as any;
  searchCouponForm = null as any;
  updateForm = null as any;

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

    this.searchCouponForm = this.fb.group({
      couponCode: ['', Validators.required]
    });

    this.updateForm = this.fb.group({
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
    this.updateMessage.set(null);
    this.searchError.set(null);
    this.selectedCoupon.set(null);
    this.searchCouponForm.reset({ couponCode: '' });
  }

  private extractDateOnly(dateTime: string): string {
    return dateTime?.split('T')[0] ?? '';
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

  private isExpired(coupon: CouponModel): boolean {
    if (!coupon?.validTill) {
      return false;
    }

    const validTillDate = new Date(coupon.validTill).getTime();
    return !Number.isNaN(validTillDate) && Date.now() > validTillDate;
  }

  getCouponStatus(coupon: CouponModel): string {
    if (this.isExpired(coupon)) {
      return 'Expired';
    }

    return coupon.isActive ? 'Active' : 'Inactive';
  }

  toggleSortDirection() {
    this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
  }

  private sortCoupons(coupons: CouponModel[]): CouponModel[] {
    const field = this.selectedSortField();
    const direction = this.sortDirection() === 'desc' ? -1 : 1;

    return [...coupons].sort((a, b) => {
      let aValue: string | number = '';
      let bValue: string | number = '';

      switch (field) {
        case 'status':
          aValue = this.getCouponStatus(a);
          bValue = this.getCouponStatus(b);
          break;
        case 'validTill':
          aValue = new Date(a.validTill).getTime();
          bValue = new Date(b.validTill).getTime();
          break;
        case 'discountPercentage':
          aValue = a.discountPercentage ?? 0;
          bValue = b.discountPercentage ?? 0;
          break;
        case 'couponCode':
        default:
          aValue = a.couponCode ?? '';
          bValue = b.couponCode ?? '';
          break;
      }

      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return (aValue - bValue) * direction;
      }

      return aValue.toString().localeCompare(bValue.toString()) * direction;
    });
  }

  private validDateRangeValidator = (group: any) => {
    const validFrom = group.get('validFrom')?.value;
    const validTill = group.get('validTill')?.value;

    if (!validFrom || !validTill) {
      return null;
    }

    return validTill <= validFrom ? { validTillBeforeValidFrom: true } : null;
  };

  searchCouponByCode() {
    if (this.searchCouponForm.invalid) {
      this.searchCouponForm.markAllAsTouched();
      return;
    }

    this.isUpdateLoading.set(true);
    this.searchError.set(null);
    this.updateMessage.set(null);
    this.selectedCoupon.set(null);

    const couponCode = this.searchCouponForm.value.couponCode.trim();
    this.couponService.fetchCouponByCode(couponCode)
      .pipe(finalize(() => this.isUpdateLoading.set(false)))
      .subscribe({
        next: coupon => {
          this.selectedCoupon.set(coupon);
          this.updateForm.reset({
            couponCode: coupon.couponCode,
            description: coupon.description,
            discountPercentage: coupon.discountPercentage,
            discountAmount: coupon.discountAmount,
            minimumBookingAmount: coupon.minimumBookingAmount,
            maximumDiscountAmount: coupon.maximumDiscountAmount,
            usageLimit: coupon.usageLimit,
            validFrom: this.extractDateOnly(coupon.validFrom),
            validTill: this.extractDateOnly(coupon.validTill),
          });
        },
        error: err => {
          console.error('Failed to find coupon by code', err);
          this.searchError.set('Coupon not found. Please verify the code and try again.');
        }
      });
  }

  onUpdateCoupon() {
    if (this.updateForm.invalid) {
      this.updateForm.markAllAsTouched();
      return;
    }

    const selected = this.selectedCoupon();
    if (!selected) {
      this.searchError.set('Please search for a coupon before updating.');
      return;
    }

    this.isUpdateLoading.set(true);
    this.updateMessage.set(null);

    const raw = this.updateForm.value;
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

    this.couponService.updateCoupon(selected.id, payload)
      .pipe(finalize(() => this.isUpdateLoading.set(false)))
      .subscribe({
        next: updated => {
          this.selectedCoupon.set(updated);
          this.updateMessage.set('Coupon updated successfully.');
          const current = this.coupons();
          this.coupons.set(current.map(item => item.id === updated.id ? updated : item));
        },
        error: err => {
          console.error('Failed to update coupon', err);
          this.updateMessage.set('Failed to update coupon. Please try again.');
        }
      });
  }

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

    this.couponService.createCoupon(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
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
        }
      });
  }
}

