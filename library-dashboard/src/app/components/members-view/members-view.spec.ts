import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembersView } from './members-view';

describe('MembersView', () => {
  let component: MembersView;
  let fixture: ComponentFixture<MembersView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MembersView],
    }).compileComponents();

    fixture = TestBed.createComponent(MembersView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
