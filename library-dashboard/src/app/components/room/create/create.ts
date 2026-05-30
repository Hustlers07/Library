import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { progressLoading } from '../../../constants/api.constants';
import { RoomService } from '../../../services/room-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-create',
  imports: [
    MatAutocompleteModule,
    MatCardModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './create.html',
  styleUrl: './create.scss',
})
export class Create {

  createForm: FormGroup;

  floors = ['FLOOR_GF', 'FLOOR_FF', 'FLOOR_SF','FLOOR_TF'];

  constructor(private fb: FormBuilder, private roomService: RoomService, private snackBar: MatSnackBar) {
    this.createForm = this.fb.group({
      houseNo: ['', [Validators.required]],
      floor: ['', [Validators.required]],
      location: ['', [Validators.required]],
      description: [''],
    });
  }

  onSubmit() {

    console.log("Creating room: ", this.createForm.value);

    progressLoading.set(true);

    this.roomService.createRoom(this.createForm.value).subscribe({
      next: (resp) => {
        console.log("Created room response ", resp);
        this.snackBar.open("Created room with id: " + resp.id);
      },
      error: (error: HttpErrorResponse) => {

        console.error("Error while creating room", error);
        progressLoading.set(false);

        const errorMsg = error.message || 'Unexpected error occured';

        this.snackBar.open("Failed to create room: " + errorMsg);
      },
      complete: () => {
        this.createForm.reset();
        progressLoading.set(false);

      }
    })

  }
}
