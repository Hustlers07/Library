import { Component, Input, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RoomService } from '../../../services/room-service';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';

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
    MatSelectModule,
    MatSnackBarModule,
    CommonModule
  ],
  templateUrl: './update.html',
  styleUrl: './update.scss',
})
export class Update {
  @Input() room: any | null = null;

  updateForm: FormGroup;
  addUserForm: FormGroup;
  addSeatForm: FormGroup;
  disableSeatForm: FormGroup;
  floors = ['FLOOR_GF', 'FLOOR_FF', 'FLOOR_SF', 'FLOOR_TF'];
  statuses = ['ROOM_VACANT', 'ROOM_OCCUPIED', 'ROOM_MAINTENANCE'];

  defaultRoom = {
    id: 1,
    status: 'ROOM_VACANT',
    houseNo: 'B 9/2',
    floor: 'FLOOR_GF',
    location: 'QUTUB VIHAR PHASE-2',
    description: 'QUTUB VIHAR',
    createdAt: '2026-05-26T19:20:55.728052',
    updatedAt: '2026-05-26T19:20:55.728058',
  };

  get activeRoom() {
    return this.room ?? this.defaultRoom;
  }

  constructor(
    private fb: FormBuilder,
    private roomService: RoomService,
    private snackBar: MatSnackBar
  ) {
    this.updateForm = this.fb.group({
      status: [this.defaultRoom.status, [Validators.required]],
      houseNo: [this.defaultRoom.houseNo, [Validators.required]],
      floor: [this.defaultRoom.floor, [Validators.required]],
      location: [this.defaultRoom.location, [Validators.required]],
      description: [this.defaultRoom.description],
    });

    this.addUserForm = this.fb.group({
      username: ['', [Validators.required]],
    });

    this.addSeatForm = this.fb.group({
      seatCount: [5, [Validators.required, Validators.min(1)]],
    });

    this.disableSeatForm = this.fb.group({
      seatId: [null, [Validators.required, Validators.min(1)]],
    });
  }

  availableSeats: string[] = [];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['room'] && this.room) {
      this.loadAvailableSeats();
      // patch form values when input room changes
      this.updateForm.patchValue({
        status: this.room.status ?? this.updateForm.value.status,
        houseNo: this.room.houseNo ?? this.updateForm.value.houseNo,
        floor: this.room.floor ?? this.updateForm.value.floor,
        location: this.room.location ?? this.updateForm.value.location,
        description: this.room.description ?? this.updateForm.value.description,
      });
    }
  }

  onSubmit() {
    if (this.updateForm.invalid) {
      return;
    }

    const active = this.activeRoom;
    const payload = {
      ...this.updateForm.value,
      id: active.id,
    };

    console.log('Updating room:', payload);
    this.roomService.updateRoom(active.id, payload).subscribe({
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
    const active = this.activeRoom;
    this.roomService.addUserToRoom(active.id, username).subscribe({
      next: () => {
        this.snackBar.open(`User '${username}' added to room ${active.id}`, 'Close', { duration: 3000 });
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
    const active = this.activeRoom;
    this.roomService.addSeatsToRoom(active.id, seatCount).subscribe({
      next: () => {
        this.snackBar.open(`Added ${seatCount} seats to room ${active.id}`, 'Close', { duration: 3000 });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error while adding seats to room', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to add seats: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }

  private loadAvailableSeats() {
    const seats = this.room?.seats ?? this.room?.availableSeats ?? this.room?.seatIds ?? [];
    if (Array.isArray(seats)) {
      this.availableSeats = seats
        .map((seat: any) => typeof seat === 'string' ? seat : seat?.seatId ?? seat?.seatId ?? null)
        .filter((id: any): id is string => typeof id === 'string');
    } else {
      this.availableSeats = [];
    }
  }

  onDisableSeat() {
    if (this.disableSeatForm.invalid) {
      return;
    }

    const seatId = this.disableSeatForm.value.seatId;
    const active = this.activeRoom;
    this.roomService.disableSeat(seatId).subscribe({
      next: () => {
        this.snackBar.open(`Seat ${seatId} disabled`, 'Close', { duration: 3000 });
        this.disableSeatForm.reset({ seatId: null });
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error while disabling seat', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to disable seat: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }
}

