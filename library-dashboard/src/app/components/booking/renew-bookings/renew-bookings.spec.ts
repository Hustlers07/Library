import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RenewBookings } from './renew-bookings';

describe('RenewBookings', () => {
  let component: RenewBookings;
  let fixture: ComponentFixture<RenewBookings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RenewBookings],
    }).compileComponents();

    fixture = TestBed.createComponent(RenewBookings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
