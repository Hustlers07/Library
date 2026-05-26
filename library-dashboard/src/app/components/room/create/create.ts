import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { progressLoading } from '../../../constants/api.constants';

@Component({
  selector: 'app-create',
  imports: [
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

  constructor(private fb: FormBuilder){
    this.createForm = this.fb.group({
      houseNo: ['', [Validators.required]],
      floor: ['', [Validators.required]],
      location: ['', [Validators.required]],
      description: [''],
    });
  }

  onSubmit(){

    if(this.createForm.invalid){
      console.log("Invalid form submission, details : ", this.createForm.value);
      return;
    }

    progressLoading.set(true);
    console.log("Creating room: ", this.createForm.value);
  }
}
