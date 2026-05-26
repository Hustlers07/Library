import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { RoomService } from '../../../services/room-service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-seat',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    CommonModule,
  ],
  templateUrl: './seat.html',
  styleUrl: './seat.scss',
})
export class Seat {
  mapForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private roomService: RoomService,
    private snackBar: MatSnackBar
  ) {
    this.mapForm = this.fb.group({
      seatId: [null, [Validators.required, Validators.min(1)]],
      username: ['', [Validators.required]],
    });
  }

  onMapUser() {
    if (this.mapForm.invalid) {
      return;
    }

    const seatId = this.mapForm.value.seatId;
    const username = this.mapForm.value.username.trim();

    this.roomService.mapUserToSeat(seatId, username).subscribe({
      next: () => {
        this.snackBar.open(`Mapped user '${username}' to seat ${seatId}`, 'Close', { duration: 3000 });
        this.mapForm.reset({ seatId: null, username: '' });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error mapping user to seat', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to map user: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }
}
