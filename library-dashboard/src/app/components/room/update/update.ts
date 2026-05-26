import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RoomService } from '../../../services/room-service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-update',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatAutocompleteModule,
    MatDividerModule,
    MatSnackBarModule,
  ],
  templateUrl: './update.html',
  styleUrl: './update.scss',
})
export class Update {

  updateForm: FormGroup;
  addUserForm: FormGroup;
  addSeatForm: FormGroup;
  floors = ['FLOOR_GF', 'FLOOR_FF', 'FLOOR_SF', 'FLOOR_TF'];
  statuses = ['ROOM_VACANT', 'ROOM_OCCUPIED', 'ROOM_MAINTENANCE'];

  room = {
    id: 1,
    status: 'ROOM_VACANT',
    houseNo: 'B 9/2',
    floor: 'FLOOR_GF',
    location: 'QUTUB VIHAR PHASE-2',
    description: 'QUTUB VIHAR',
    createdAt: '2026-05-26T19:20:55.728052',
    updatedAt: '2026-05-26T19:20:55.728058',
  };

  constructor(
    private fb: FormBuilder,
    private roomService: RoomService,
    private snackBar: MatSnackBar
  ) {
    this.updateForm = this.fb.group({
      status: [this.room.status, [Validators.required]],
      houseNo: [this.room.houseNo, [Validators.required]],
      floor: [this.room.floor, [Validators.required]],
      location: [this.room.location, [Validators.required]],
      description: [this.room.description],
    });

    this.addUserForm = this.fb.group({
      username: ['', [Validators.required]],
    });

    this.addSeatForm = this.fb.group({
      seatCount: [5, [Validators.required, Validators.min(1)]],
    });
  }

  onSubmit() {
    if (this.updateForm.invalid) {
      return;
    }

    const payload = {
      ...this.updateForm.value,
      id: this.room.id,
    };

    console.log('Updating room:', payload);
    this.roomService.updateRoom(this.room.id, payload).subscribe({
      next: (resp) => {
        this.snackBar.open(`Room ${resp.id} updated successfully`, 'Close', { duration: 3000 });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error while updating room', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to update room: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }

  onAddUser() {
    if (this.addUserForm.invalid) {
      return;
    }

    const username = this.addUserForm.value.username.trim();
    this.roomService.addUserToRoom(this.room.id, username).subscribe({
      next: () => {
        this.snackBar.open(`User '${username}' added to room ${this.room.id}`, 'Close', { duration: 3000 });
        this.addUserForm.reset({ username: '' });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error while adding user to room', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to add user: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }

  onAddSeats() {
    if (this.addSeatForm.invalid) {
      return;
    }

    const seatCount = this.addSeatForm.value.seatCount;
    this.roomService.addSeatsToRoom(this.room.id, seatCount).subscribe({
      next: () => {
        this.snackBar.open(`Added ${seatCount} seats to room ${this.room.id}`, 'Close', { duration: 3000 });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error while adding seats to room', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to add seats: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }
}

