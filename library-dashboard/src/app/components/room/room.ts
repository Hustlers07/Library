import { Component, OnInit, signal } from '@angular/core';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';
import { RoomService, RoomObj } from '../../services/room-service';
import { progressLoading } from '../../constants/api.constants';
import { Create } from './create/create';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule} from '@angular/material/grid-list';
import { CommonModule } from '@angular/common'

@Component({
  selector: 'app-room',
  imports: [MatButtonToggleModule, Create, MatListModule, MatExpansionModule, MatGridListModule, CommonModule],
  templateUrl: './room.html',
  styleUrl: './room.scss',
})
export class Room implements OnInit {



  constructor(private roomService: RoomService) { }

  actions = [
    {
      key: 'View',
      value: 'view'
    },
    {
      key: 'Create',
      value: 'create'
    },
    {
      key: 'Update',
      value: 'update'
    },
    {
      key: 'Delete',
      value: 'delete'
    }
  ];

  selectedChange = signal(this.actions[0].value)

  rooms: RoomObj[] = [];

  onActionChange(event: MatButtonToggleChange): void {
    console.log(event.value);   // 'create' | 'update' | 'delete'
    this.selectedChange.set(event.value);
  }

  ngOnInit(): void {

    this.loadAllRooms();
  }

  loadAllRooms() {

    progressLoading.set(true);

    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms;
        console.log("Rooms list : ", rooms)
      },
      error: (err) => {
        console.log("No room available");
        progressLoading.set(false);
      },
      complete() {
        progressLoading.set(false);
      }
    });
  }


}
