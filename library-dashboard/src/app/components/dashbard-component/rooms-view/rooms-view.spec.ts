import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomsView } from './rooms-view';

describe('RoomsView', () => {
  let component: RoomsView;
  let fixture: ComponentFixture<RoomsView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomsView],
    }).compileComponents();

    fixture = TestBed.createComponent(RoomsView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
