import { Component, signal } from '@angular/core';
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
import { Router } from '@angular/router';
import { progressLoading, ROUTES } from '../../constants/api.constants';


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
  
  errorMessage = signal('');

  ngOnInit() {
    progressLoading.set(false);
    console.log('API Base URL from ConfigService:', this.configService.apiUrl);
  }


  constructor(private fb: FormBuilder,
    private configService: ConfigService,
    private authService: AuthService,
    private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }


  onSubmit() {
    console.log('API Base URL from ConfigService:', this.configService.apiUrl);
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
      progressLoading.set(true);
      this.authService.login(this.loginForm.value).subscribe({
        next: (token) => {
          // console.log('Login successful, token:', token);
          // Optionally navigate to another page or show success message
          this.router.navigate([ROUTES.DASHBOARD]); // Example navigation after successful login
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.error || 'Login failed. Please try again.');
          console.error('Login error:', err);
          progressLoading.set(false);
        },
        complete() {
          progressLoading.set(false);
        },
      });
    }
  }
}
