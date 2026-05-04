import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterMember } from './register-member';

describe('RegisterMember', () => {
  let component: RegisterMember;
  let fixture: ComponentFixture<RegisterMember>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterMember],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterMember);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
