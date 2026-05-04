import { Component, inject } from '@angular/core';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MembersView } from "../members-view/members-view";
import { SeatsView } from "../seats-view/seats-view";
import { PaymentsView } from "../payments-view/payments-view";
import { RoomsView } from "../rooms-view/rooms-view";

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
    MembersView,
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

 
  ngOnInit() {
    console.log('Dashboard component initialized');
    
  }
}
