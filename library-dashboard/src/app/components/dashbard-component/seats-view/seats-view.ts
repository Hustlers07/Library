import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { RoomService, RoomObj } from '../../../services/room-service';

interface SeatObj {
  id: number;
  seatId: string;
  users: unknown;
  pricePerHour: number;
  createdAt: string;
  updatedAt: string;
  active: boolean;
  roomHouse?: string;
  roomLocation?: string;
}

interface RoomWithSeats extends RoomObj {
  seats?: SeatObj[];
}

@Component({
  selector: 'app-seats-view',
  imports: [CommonModule, MatCardModule],
  templateUrl: './seats-view.html',
  styleUrl: './seats-view.scss',
})
export class SeatsView implements OnInit {
  seatCards: SeatObj[] = [];

  constructor(private roomService: RoomService) {}

  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.seatCards = (rooms as RoomWithSeats[])
          .flatMap((room) =>
            (room.seats ?? []).map((seat) => ({
              ...seat,
              roomHouse: room.houseNo,
              roomLocation: room.location,
            }))
          );
      },
      error: (error) => {
        console.error('Failed to load seats', error);
      },
    });
  }

  isAvailable(seat: SeatObj): boolean {
    return seat.users == null && seat.active === false;
  }
}
