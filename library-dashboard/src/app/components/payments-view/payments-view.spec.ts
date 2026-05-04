import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentsView } from './payments-view';

describe('PaymentsView', () => {
  let component: PaymentsView;
  let fixture: ComponentFixture<PaymentsView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentsView],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentsView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
