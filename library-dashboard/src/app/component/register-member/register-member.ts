import { Component } from '@angular/core';
import { RegisterationForm } from "../registeration-form/registeration-form";

@Component({
  selector: 'app-register-member',
  imports: [RegisterationForm],
  templateUrl: './register-member.html',
  styleUrl: './register-member.scss',
})
export class RegisterMember {}
