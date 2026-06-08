import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingsView } from './bookings-view/bookings-view';
import { MatButtonToggleModule, MatButtonToggleChange } from '@angular/material/button-toggle';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, BookingsView, MatButtonToggleModule],
  templateUrl: './booking.html',
  styleUrls: ['./booking.scss'],
})
export class Booking {
  selectedAction: 'view' | 'create' | 'renew' | 'cancel' = 'view';
  actions: Array<{ key: string; value: 'view' | 'create' | 'renew' | 'cancel' }> = [
    { key: 'View', value: 'view' },
    { key: 'Create', value: 'create' },
    { key: 'Renew', value: 'renew' },
    { key: 'Cancel', value: 'cancel' }
  ];

  onActionChange(event: MatButtonToggleChange) {
    this.selectedAction = event.value as 'view' | 'create' | 'renew' | 'cancel';
  }
}
