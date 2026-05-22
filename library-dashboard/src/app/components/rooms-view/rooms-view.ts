import { Component } from '@angular/core';
import {TitleCasePipe} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';

@Component({
  selector: 'app-rooms-view',
  imports: [MatListModule, MatIconModule, TitleCasePipe],
  templateUrl: './rooms-view.html',
  styleUrl: './rooms-view.scss',
})
export class RoomsView {
onClick($event: PointerEvent) {
throw new Error('Method not implemented.');
}
  fragments = ['inbox', 'outbox', 'drafts'];
  activeLink: string = "no";
}
