import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatsView } from './seats-view';

describe('SeatsView', () => {
  let component: SeatsView;
  let fixture: ComponentFixture<SeatsView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeatsView],
    }).compileComponents();

    fixture = TestBed.createComponent(SeatsView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
