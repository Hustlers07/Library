import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomSidenav } from './custom-sidenav';

describe('CustomSidenav', () => {
  let component: CustomSidenav;
  let fixture: ComponentFixture<CustomSidenav>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomSidenav],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomSidenav);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
