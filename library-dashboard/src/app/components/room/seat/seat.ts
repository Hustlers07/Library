import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { RoomService, RoomObj } from '../../../services/room-service';
import { HttpErrorResponse } from '@angular/common/http';

interface SeatObj {
  id: number;
  seatId: string;
  users: any;
  pricePerHour: number;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

interface RoomWithSeats extends RoomObj {
  seats?: SeatObj[];
}

@Component({
  selector: 'app-seat',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    CommonModule,
  ],
  templateUrl: './seat.html',
  styleUrls: ['./seat.scss'],
})
export class Seat implements OnInit {
  mapForm: FormGroup;
  rooms: RoomWithSeats[] = [];
  selectedRoom: RoomWithSeats | null = null;
  availableSeats: SeatObj[] = [];

  constructor(
    private fb: FormBuilder,
    private roomService: RoomService,
    private snackBar: MatSnackBar
  ) {
    this.mapForm = this.fb.group({
      roomId: [null, [Validators.required]],
      seatId: [null, [Validators.required, Validators.min(1)]],
      username: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.loadRooms();
  }

  loadRooms(): void {
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms as RoomWithSeats[];
      },
      error: (err) => {
        console.error('Error loading rooms', err);
      },
    });
  }

  onRoomChange(roomId: number | null): void {
    this.selectedRoom = this.rooms.find(room => room.id === roomId) ?? null;
    this.availableSeats = this.selectedRoom?.seats?.filter(seat => seat.active === true && seat.users == null) ?? [];
    this.mapForm.patchValue({ seatId: null });
  }

  onMapUser() {
    if (this.mapForm.invalid) {
      return;
    }

    const seatId = this.mapForm.value.seatId;
    const username = this.mapForm.value.username.trim();

    console.log('Mapping user to seat', { seatId, username });

    this.roomService.mapUserToSeat(seatId, username).subscribe({
      next: () => {
        this.snackBar.open(`Mapped user '${username}' to seat ${seatId}`, 'Close', { duration: 3000 });
        this.mapForm.reset({ seatId: null, username: '' });
        this.loadRooms();
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error mapping user to seat', error);
        const errorMsg = error.message || 'Unexpected error occurred';
        this.snackBar.open(`Failed to map user: ${errorMsg}`, 'Close', { duration: 4000 });
      },
    });
  }
}
