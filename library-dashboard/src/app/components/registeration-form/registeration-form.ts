import { Component, Input, signal } from '@angular/core';
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

@Component({
  selector: 'app-registeration-form',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    RouterLink],
  templateUrl: './registeration-form.html',
  styleUrl: './registeration-form.scss',
})
export class RegisterationForm {

  @Input() title:string = '';
  @Input() route: string = '';

    LOGIN = ROUTES.LOGIN;
    registerForm: FormGroup;
    hidePassword = true;
    hideConfirmPassword = true;
    
    errorMessage = signal('');
  
    ngOnInit() {
      progressLoading.set(false);
      console.log('API Base URL from ConfigService:', this.configService.apiUrl);
    }
  
  
    constructor(private fb: FormBuilder,
      private configService: ConfigService,
      private authService: AuthService,
      private router: Router) {
      this.registerForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required],
        firstName: [''],
        lastName: [''],
        phoneNumber: ['', Validators.pattern(/^[6-9]\d{9}$/)], // E.164 format  
      }, { validators: this.passwordMatchValidator });
    }
  
    passwordMatchValidator(form: FormGroup) {
      const password = form.get('password')?.value;
      const confirmPassword = form.get('confirmPassword')?.value;
      if (password !== confirmPassword) {
        form.get('confirmPassword')?.setErrors({ mismatch: true });
      } else {
        form.get('confirmPassword')?.setErrors(null);
      };
    }
  
  
    onSubmit() {
      console.log('API Base URL from ConfigService:', this.configService.apiUrl);
      if (this.registerForm.valid) {
        console.log(this.registerForm.value);
        progressLoading.set(true);
        this.authService.register(this.registerForm.value).subscribe({
          next: (token) => {
            console.log('Registration successful, token:', token);
            // Optionally navigate to another page or show success message
            this.router.navigate([ROUTES.DASHBOARD]); // Example navigation after successful registration
          },
          error: (err) => {
            this.errorMessage.set(err?.error?.error || 'Registration failed. Please try again.');
            console.error('Registration error:', err);
            progressLoading.set(false);
          },
          complete() {
            progressLoading.set(false);
          },
        });
      }
    }
}
