import { Component, OnInit, signal } from '@angular/core';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';
import { RoomService } from '../../services/room-service';
import { FormControl } from '@angular/forms';



@Component({
  selector: 'app-room',
  imports: [MatButtonToggleModule],
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
  selectedChange: string=this.actions[0].value;

  onActionChange(event: MatButtonToggleChange): void {
    console.log(event.value);   // 'create' | 'update' | 'delete'
    this.selectedChange = event.value;
  }
  ngOnInit(): void {

  }


}
