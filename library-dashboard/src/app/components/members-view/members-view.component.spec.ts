import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { MembersViewComponent } from './members-view.component';

describe('MembersViewComponent', () => {
  let component: MembersViewComponent;
  let fixture: ComponentFixture<MembersViewComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MembersViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
