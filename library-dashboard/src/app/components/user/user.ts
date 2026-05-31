import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule, MatButtonToggleChange } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { UserService } from '../../services/user-service';
import { User } from '../../models/user/user-module';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonToggleModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatCheckboxModule],
  templateUrl: './user.html',
  styleUrls: ['./user.scss'],
})
export class UserComponent implements OnInit {
  selectedAction = 'view';
  actionOptions = [
    { key: 'View', value: 'view' },
    { key: 'Register', value: 'register' },
    { key: 'Update', value: 'update' },
    { key: 'Change Password', value: 'password' },
  ];

  users = signal<User[]>([]);
  isLoading = signal(true);
  isSubmitting = signal(false);
  isUpdateLoading = signal(false);
  actionMessage = signal<string | null>(null);
  searchError = signal<string | null>(null);
  selectedUser = signal<User | null>(null);

  registerForm = null as any;
  searchForm = null as any;
  updateForm = null as any;
  passwordForm = null as any;

  constructor(private fb: FormBuilder, private userService: UserService) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
    });

    this.searchForm = this.fb.group({
      username: ['', Validators.required],
    });

    this.updateForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      isActive: [true],
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmNewPassword: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.fetchUsers();
  }

  onActionChange(event: MatButtonToggleChange) {
    this.selectedAction = event.value;
    this.actionMessage.set(null);
    this.searchError.set(null);
    this.selectedUser.set(null);
    this.searchForm.reset({ username: '' });
  }

  fetchUsers() {
    this.isLoading.set(true);
    this.userService.fetchUsers().pipe(finalize(() => this.isLoading.set(false))).subscribe({
      next: users => this.users.set(users),
      error: err => {
        console.error('Unable to load users', err);
        this.users.set([]);
      }
    });
  }

  onRegisterMember() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const raw = this.registerForm.value;
    if (raw.password !== raw.confirmPassword) {
      this.actionMessage.set('Passwords do not match.');
      return;
    }

    this.isSubmitting.set(true);
    this.actionMessage.set(null);

    this.userService.registerMember({
      email: raw.email,
      password: raw.password,
      confirmPassword: raw.confirmPassword,
      firstName: raw.firstName,
      lastName: raw.lastName,
      phoneNumber: raw.phoneNumber,
    }).pipe(finalize(() => this.isSubmitting.set(false))).subscribe({
      next: () => {
        this.actionMessage.set('Member registered successfully.');
        this.registerForm.reset();
        this.fetchUsers();
      },
      error: err => {
        console.error('Register member failed', err);
        this.actionMessage.set(err?.message || 'Failed to register member.');
      }
    });
  }

  searchUserByUsername() {
    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    this.searchError.set(null);
    this.isUpdateLoading.set(true);
    const username = this.searchForm.value.username.trim().toLowerCase();

    this.userService.fetchUsers().pipe(finalize(() => this.isUpdateLoading.set(false))).subscribe({
      next: users => {
        const found = users.find(user => user.username?.toLowerCase() === username || user.email?.toLowerCase() === username);
        if (!found) {
          this.searchError.set('User not found. Please check the username or email.');
          this.selectedUser.set(null);
          return;
        }
        this.selectedUser.set(found);
        this.updateForm.reset({
          email: found.email,
          firstName: found.firstName,
          lastName: found.lastName,
          phoneNumber: found.phoneNumber,
          isActive: found.isActive,
        });
      },
      error: err => {
        console.error('Search user failed', err);
        this.searchError.set('Unable to search users.');
      }
    });
  }

  onUpdateMember() {
    if (this.updateForm.invalid) {
      this.updateForm.markAllAsTouched();
      return;
    }

    const selected = this.selectedUser();
    if (!selected) {
      this.searchError.set('Please search for a user before updating.');
      return;
    }

    this.isUpdateLoading.set(true);
    this.actionMessage.set(null);
    const raw = this.updateForm.value;

    this.userService.updateUser(selected.id, {
      email: raw.email,
      firstName: raw.firstName,
      lastName: raw.lastName,
      phoneNumber: raw.phoneNumber,
      isActive: raw.isActive,
    }).pipe(finalize(() => this.isUpdateLoading.set(false))).subscribe({
      next: updated => {
        this.actionMessage.set('Member updated successfully.');
        this.selectedUser.set(updated);
        this.users.set(this.users().map(user => user.id === updated.id ? updated : user));
      },
      error: err => {
        console.error('Update user failed', err);
        this.actionMessage.set(err?.message || 'Failed to update member.');
      }
    });
  }

  toggleActive(user: User) {
    this.isUpdateLoading.set(true);
    this.actionMessage.set(null);
    this.userService.setUserActive(user.id, !user.isActive).pipe(finalize(() => this.isUpdateLoading.set(false))).subscribe({
      next: updated => {
        this.actionMessage.set(updated.isActive ? 'Member activated.' : 'Member deactivated.');
        this.users.set(this.users().map(item => item.id === updated.id ? updated : item));
        if (this.selectedUser()?.id === updated.id) {
          this.selectedUser.set(updated);
          this.updateForm.patchValue({ isActive: updated.isActive });
        }
      },
      error: err => {
        console.error('Toggle active failed', err);
        this.actionMessage.set(err?.message || 'Failed to update member status.');
      }
    });
  }

  onChangePassword() {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const raw = this.passwordForm.value;
    if (raw.newPassword !== raw.confirmNewPassword) {
      this.actionMessage.set('New passwords do not match.');
      return;
    }

    this.isSubmitting.set(true);
    this.actionMessage.set(null);

    this.userService.changePassword({
      currentPassword: raw.currentPassword,
      newPassword: raw.newPassword,
    }).pipe(finalize(() => this.isSubmitting.set(false))).subscribe({
      next: message => {
        this.actionMessage.set(message || 'Password changed successfully.');
        this.passwordForm.reset();
      },
      error: err => {
        console.error('Password change failed', err);
        this.actionMessage.set(err?.message || 'Failed to change password.');
      }
    });
  }

  selectUserForUpdate(user: User) {
    this.selectedAction = 'update';
    this.selectedUser.set(user);
    this.searchForm.reset({ username: user.username });
    this.updateForm.reset({
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      phoneNumber: user.phoneNumber,
      isActive: user.isActive,
    });
  }
}
