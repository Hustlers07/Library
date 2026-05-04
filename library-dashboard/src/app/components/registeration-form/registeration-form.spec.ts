import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterationForm } from './registeration-form';

describe('RegisterationForm', () => {
  let component: RegisterationForm;
  let fixture: ComponentFixture<RegisterationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterationForm],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterationForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
