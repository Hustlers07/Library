import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { RoomService, RoomObj } from '../../../services/room-service';
import { MatGridListModule } from '@angular/material/grid-list';
import { BreakpointObserver } from '@angular/cdk/layout';
import { MatIconModule } from '@angular/material/icon';
import { progressLoading } from '../../../constants/api.constants';

interface SeatObj {
  id: number;
  seatId: string;
  user?: unknown;
  users?: unknown;
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
  imports: [CommonModule, MatCardModule, MatGridListModule, MatIconModule],
  templateUrl: './seats-view.html',
  styleUrls: ['./seats-view.scss'],
})
export class SeatsView implements OnInit {
  seats: SeatObj[] = [];
  gridCols = 4;
  rowHeight = '2:1';



  constructor(private roomService: RoomService, private breakpointObserver: BreakpointObserver) {


    this.breakpointObserver.observe([
      '(max-width: 400px)',   // very small screens
      '(max-width: 600px)',   // small phones
      '(max-width: 900px)',   // tablets
      '(max-width: 1200px)',  // small laptops
      '(max-width: 1600px)'   // desktops
    ]).subscribe(result => {
      if (result.breakpoints['(max-width: 400px)']) {
        this.gridCols = 1;
      } else if (result.breakpoints['(max-width: 600px)']) {
        this.gridCols = 2;
      } else if (result.breakpoints['(max-width: 900px)']) {
        this.gridCols = 3;
      } else if (result.breakpoints['(max-width: 1200px)']) {
        this.gridCols = 4;
      } else if (result.breakpoints['(max-width: 1600px)']) {
        this.gridCols = 6;
      } else {
        this.gridCols = 10; // very large screens
      }
    });
  }

  ngOnInit(): void {
    progressLoading.set(true);
    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        console.log('Loaded rooms with seats', rooms);
        progressLoading.set(false);
        this.seats = (rooms as RoomWithSeats[])
          .flatMap((room) =>
            (room.seats ?? []).map((seat) => ({
              ...seat,
              roomHouse: room.houseNo,
              roomLocation: room.location,
            }))
          );
      },
      error: (error) => {
        progressLoading.set(false);
        console.error('Failed to load seats', error);
      },
    });
  }

  isAvailable(seat: SeatObj): boolean {
    const seatUsers = Array.isArray(seat.users)
      ? seat.users
      : Array.isArray((seat as any).user)
        ? (seat as any).user
        : seat.users;

    const hasNoUsers = seatUsers == null || (Array.isArray(seatUsers) && seatUsers.length === 0);
    return hasNoUsers && seat.active !== false;
  }
}
