import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { progressLoading, ROUTES } from '../../constants/api.constants';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule
  ],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.scss'],
})
export class ResetPassword {


  LOGIN = ROUTES.LOGIN;
  passwordUpdateForm: FormGroup;
  hidePassword = true;
  
  message = signal('');
  messageColor = signal(''); // 'success' or 'error'

  ngOnInit() {
    progressLoading.set(false);
    console.log('API Base URL from ConfigService:', environment.apiUrl);
  }


  constructor(private fb: FormBuilder,
    private authService: AuthService,
    private router: Router) {
    this.passwordUpdateForm = this.fb.group({
      username: ['', [Validators.required]],
      newPassword: ['', Validators.required]
    });
  }


  onSubmit() {
    // console.log('API Base URL from ConfigService:', this.configService.apiUrl);
    let message = '';
    if (this.passwordUpdateForm.valid) {
      console.log(this.passwordUpdateForm.value);
      progressLoading.set(true);
      this.authService.changePassword(this.passwordUpdateForm.value).subscribe({
        next: (msg) => {
          this.passwordUpdateForm.reset({username: '', newPassword: ''});
          this.messageColor.set('green');
          this.message.set(msg || 'Password updated successfully');
          
        },
        error: (err) => {
          this.messageColor.set('red');
          this.message.set(err?.error?.error || 'Update failed. Please try again.');
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
