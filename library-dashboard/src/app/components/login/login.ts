import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { ConfigService } from '../../services/config-service';
import { AuthService } from '../../services/auth-service';
import { Router, RouterLink } from '@angular/router';
import { progressLoading, ROUTES } from '../../constants/api.constants';
import { environment } from '../../../environments/environment';


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
    MatChipsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {


  REGISTER = ROUTES.REGISTER;
  loginForm: FormGroup;
  hidePassword = true;

  errorMessage = signal('');



  constructor(private fb: FormBuilder,
    private configService: ConfigService,
    private authService: AuthService,
    private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    
  }

  ngOnInit() {
    progressLoading.set(false);
    // Pre-fill for dev environment
    // console.log("Active env: ", environment);
    if (!environment.production) {
      this.loginForm.patchValue({
        email: environment.devEmail,   // or environment.login.username
        password: environment.devPassword // or environment.login.password
      });
      // this.onSubmit();
    }
  }


  onSubmit() {
    console.log('API Base URL from ConfigService:', this.configService.apiUrl);
    if (this.loginForm.valid) {
      console.log(this.loginForm.value);
      progressLoading.set(true);
      this.authService.login(this.loginForm.value).subscribe({
        next: (token) => {
          console.log('Login successful, token:', token);
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
