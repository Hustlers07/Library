import { Component, signal } from '@angular/core';
import { RegisterationForm } from "../registeration-form/registeration-form";
import { ROUTES } from '../../constants/api.constants';

@Component({
  selector: 'app-register',
  imports: [RegisterationForm],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
 redirect = ROUTES.LOGIN;
}
