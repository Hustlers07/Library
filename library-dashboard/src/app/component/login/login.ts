import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { ConfigService } from '../../service/config-service';
import { AuthService } from '../../service/auth-service';


@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {


  loginForm: FormGroup;
  hidePassword = true;

  constructor(private fb: FormBuilder,
    private configService: ConfigService,
    private authService: AuthService) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    console.log('API Base URL from ConfigService:', this.configService.apiUrl);
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
      this.authService.login(this.loginForm.value).subscribe({
        next: (token) => {
          console.log('Login successful, token:', token);
          // Optionally navigate to another page or show success message
        },
        error: (err) => {
          console.error('Login failed:', err);
          // Optionally show error message to user
        }
      });
    }
  }
}
