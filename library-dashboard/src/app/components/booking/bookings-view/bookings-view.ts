import { Component, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { BookingService, BookingObj, BookingPayload, RenewBookingPayload } from '../../../services/booking-service';

@Component({
  selector: 'app-bookings-view',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, MatListModule, MatOptionModule, MatSelectModule],
  templateUrl: './bookings-view.html',
  styleUrls: ['./bookings-view.scss'],
})
export class BookingsView implements OnInit {
  @Input() action: 'view' | 'create' | 'renew' | 'cancel' = 'view';
  bookings = signal<BookingObj[]>([]);
  message = 'Loading bookings...';

  // simple form model
  seatIdsInput = '';
  payload: Partial<BookingPayload> = {
    userName: '',
    roomId: 0,
    bookingType: 'FLOOR',
    durationType: 'HOURLY',
    duration: 0,
    specialNotes: '',
    couponCode: '',
    planName: '',
    seatIds: []
  };

  // renew UI state
  renewing = signal<Record<number, boolean>>({});
  renewInputs: Record<number, Partial<RenewBookingPayload>> = {};

  constructor(private bookingService: BookingService) {}

  ngOnInit(): void {
    this.loadBookings();
  }



  loadBookings(): void {
    this.bookingService.fetchBookings().subscribe({
      next: (b) => { this.bookings.set(b); this.message = '' },
      error: (err) => {
        console.error('Error fetching bookings', err);
        this.message = 'Error while loading bookings.';
      }
    });
  }

  createBooking(): void {
    const seatIds = this.seatIdsInput
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length)
      .map(Number);

    const payload: BookingPayload = {
      userName: this.payload.userName || '',
      roomId: Number(this.payload.roomId) || 0,
      bookingType: (this.payload.bookingType as any) || 'FLOOR',
      durationType: (this.payload.durationType as any) || 'HOURLY',
      duration: Number(this.payload.duration) || 0,
      specialNotes: this.payload.specialNotes,
      couponCode: this.payload.couponCode || undefined,
      planName: this.payload.planName || undefined,
      seatIds: seatIds.length ? seatIds : undefined,
    };

    this.bookingService.createBooking(payload).subscribe({
      next: (created) => {
        this.bookings.update(bs => [created, ...bs]);
        this.message = 'Booking created successfully.';
        // reset simple fields
        this.payload = { userName: '', roomId: 0, bookingType: 'FLOOR', durationType: 'HOURLY', duration: 0 };
        this.seatIdsInput = '';
      },
      error: (err) => {
        console.error('Error creating booking', err);
        this.message = 'Error creating booking.';
      }
    });
  }

  toggleRenew(id: number): void {
    const curr = this.renewing();
    this.renewing.set({ ...curr, [id]: !curr[id] });
    if (!this.renewInputs[id]) {
      this.renewInputs[id] = { bookingId: id, newStartTime: '' };
    }
  }

  submitRenew(id: number): void {
    const input = this.renewInputs[id];
    if (!input || !input.newStartTime) {
      this.message = 'Please provide new start time for renewal.';
      return;
    }
    const payload: RenewBookingPayload = {
      bookingId: id,
      newStartTime: input.newStartTime as string,
      renewalCount: input.renewalCount as number | undefined,
      renewalNotes: input.renewalNotes as string | undefined,
    };

    this.bookingService.renewBooking(id, payload).subscribe({
      next: (res) => {
        if (res && res.renewalBookings && res.renewalBookings.length) {
          this.bookings.update(bs => [...res.renewalBookings, ...bs]);
        }
        this.message = res.message || 'Renewal successful.';
        const curr = this.renewing();
        this.renewing.set({ ...curr, [id]: false });
      },
      error: (err) => {
        console.error('Error renewing booking', err);
        this.message = 'Error renewing booking.';
      }
    });
  }

  cancelBooking(id: number): void {
    if (!confirm('Are you sure you want to cancel booking ' + id + '?')) return;
    this.bookingService.cancelBooking(id).subscribe({
      next: () => {
        this.bookings.update(bs => bs.filter(b => b.id !== id));
        this.message = 'Booking cancelled.';
      },
      error: (err) => {
        console.error('Error cancelling booking', err);
        this.message = 'Error cancelling booking.';
      }
    });
  }
}
