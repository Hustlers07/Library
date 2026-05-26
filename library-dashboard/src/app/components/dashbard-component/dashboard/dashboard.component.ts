import { Component, inject, signal } from '@angular/core';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { SeatsView } from "../seats-view/seats-view";
import { PaymentsView } from "../../payments-view/payments-view";
import { RoomsView } from "../rooms-view/rooms-view";
import { MembersViewComponent } from "../../members-view/members-view.component";
import { UserService } from '../../../services/user-service';

export type Card = {
  title: string;
  expandUrl: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  imports: [
    AsyncPipe,
    MatGridListModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    SeatsView,
    PaymentsView,
    RoomsView
],
})
export class Dashboard {

  card: Card[] = [
    { title: 'Seats', expandUrl: "/seats" },
    { title: 'Members', expandUrl: "/members" },
    { title: 'Payments', expandUrl: "/payments" },
    { title: 'Rooms', expandUrl: "/rooms" },
  ];

  private breakpointObserver = inject(BreakpointObserver);

  /** Based on the screen size, switch from standard to one column per row */
  cards = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => {
      if (matches) {
        return [
          { title: this.card[0].title, expandUrl: this.card[0].expandUrl, cols: 1, rows: 1 },
          { title: this.card[1].title, expandUrl: this.card[1].expandUrl, cols: 1, rows: 1 },
          { title: this.card[2].title, expandUrl: this.card[2].expandUrl, cols: 1, rows: 1 },
          { title: this.card[3].title, expandUrl: this.card[3].expandUrl, cols: 1, rows: 1 },
        ];
      }

      return [
        { title: this.card[0].title, expandUrl: this.card[0].expandUrl, cols: 2, rows: 1 },
        { title: this.card[1].title, expandUrl: this.card[1].expandUrl, cols: 1, rows: 1 },
        { title: this.card[2].title, expandUrl: this.card[2].expandUrl, cols: 1, rows: 2 },
        { title: this.card[3].title, expandUrl: this.card[3].expandUrl, cols: 1, rows: 1 },
      ];
    }),
  );


  constructor(private userService: UserService){}

  members = signal(0);
  librarian = signal(0);
  admin = signal(0);
 
  ngOnInit() {
    console.log('Dashboard component initialized');
    this.userService.fetchUsers().subscribe(users=>{
      console.log("Users: ", users);
      this.members.set(users.filter(u => u.role === 'ROLE_MEMBER').length);
      this.librarian.set(users.filter(u => u.role === 'ROLE_LIBRARIAN').length);
      this.admin.set(users.filter(u => u.role === 'ROLE_ADMIN').length);
      
    })
  }
}
