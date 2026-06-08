import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelBookings } from './cancel-bookings';

describe('CancelBookings', () => {
  let component: CancelBookings;
  let fixture: ComponentFixture<CancelBookings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelBookings],
    }).compileComponents();

    fixture = TestBed.createComponent(CancelBookings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
