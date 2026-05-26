import { Component, OnInit, signal } from '@angular/core';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';
import { RoomService, RoomObj } from '../../services/room-service';
import { progressLoading } from '../../constants/api.constants';
import { Create } from './create/create';
import { Update } from './update/update';
import { MatListModule } from '@angular/material/list';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule} from '@angular/material/grid-list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common'
import { Seat } from './seat/seat';

@Component({
  selector: 'app-room',
  imports: [
    MatButtonToggleModule, 
    Create, 
    Update, 
    Seat, 
    MatListModule, 
    MatExpansionModule, 
    MatGridListModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatAutocompleteModule, 
    ReactiveFormsModule, 
    CommonModule
  ],
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
      key: 'Seat',
      value: 'seat'
    }
    
  ];

  selectedChange = signal(this.actions[0].value)

  rooms: RoomObj[] = [];
  filteredRooms: RoomObj[] = [];
  roomControl = new FormControl<RoomObj | string | null>(null);
  selectedRoom: RoomObj | null = null;

  onActionChange(event: MatButtonToggleChange): void {
    console.log(event.value);   // 'create' | 'update' | 'seat'
    this.selectedChange.set(event.value);
  }

  onSelectRoom(room: RoomObj) {
    this.selectedRoom = room;
  }

  displayRoom(room: RoomObj | string | null): string {
    if (!room) {
      return '';
    }
    return typeof room === 'string' ? room : room.houseNo;
  }

  ngOnInit(): void {
    this.roomControl.valueChanges.subscribe(value => {
      const search = typeof value === 'string' ? value : value?.houseNo ?? '';
      this.filteredRooms = this.rooms.filter(room =>
        room.houseNo.toLowerCase().includes(search.toLowerCase()) ||
        room.location.toLowerCase().includes(search.toLowerCase())
      );
    });

    this.loadAllRooms();
  }

  loadAllRooms() {

    progressLoading.set(true);

    this.roomService.getAllRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms;
        this.filteredRooms = rooms;
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
