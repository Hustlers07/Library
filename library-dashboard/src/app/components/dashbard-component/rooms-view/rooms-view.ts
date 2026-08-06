import { Component, OnInit, signal } from '@angular/core';
import {TitleCasePipe} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import { RoomObj, RoomService } from '../../../services/room-service';

@Component({
  selector: 'app-rooms-view',
  imports: [MatListModule, MatIconModule, TitleCasePipe],
  templateUrl: './rooms-view.html',
  styleUrls: ['./rooms-view.scss'],
})
export class RoomsView implements OnInit{
onClick($event: PointerEvent) {
throw new Error('Method not implemented.');
}

  constructor(private roomService: RoomService){}

  rooms= signal<RoomObj[]>([]);
  message = 'Loading please wait.';


  ngOnInit(): void {
    this.roomService.getAllRooms().subscribe({
      next: (rooms)=> this.rooms.set(rooms),
      error: (error) => {
        this.message = "Error while loading rooms.";
        console.log(error)
      },
      complete: () => console.log('Fetched rooms: ', this.rooms())
    });
  }

  

}
