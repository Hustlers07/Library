import { Component } from '@angular/core';
import { MatButtonToggleChange, MatButtonToggleModule } from '@angular/material/button-toggle';

@Component({
  selector: 'app-coupon',
  imports: [MatButtonToggleModule],
  templateUrl: './coupon.html',
  styleUrl: './coupon.scss',
})
export class Coupon {
onActionChange($event: MatButtonToggleChange) {
throw new Error('Method not implemented.');
}
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
    }
    
  ];
}
