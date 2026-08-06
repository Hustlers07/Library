import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule, MatButtonToggleChange } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { PLAN_TYPES, PlanService, Plan as PlanModel, PlanCreatePayload } from '../../services/plan-service';

@Component({
  selector: 'app-plan',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonToggleModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, MatCheckboxModule],
  templateUrl: './plan.html',
  styleUrls: ['./plan.scss'],
})
export class Plan implements OnInit {
  loading = signal(true);
  plans = signal<PlanModel[]>([]);
  selectedAction = 'view';
  isSubmitting = signal(false);
  isUpdateLoading = signal(false);
  submissionMessage = signal<string | null>(null);
  updateMessage = signal<string | null>(null);
  searchError = signal<string | null>(null);
  selectedPlan = signal<PlanModel | null>(null);

  planForm = null as any;
  searchPlanForm = null as any;
  updateForm = null as any;

  actions = [
    { key: 'View', value: 'view' },
    { key: 'Create', value: 'create' },
    { key: 'Update', value: 'update' }
  ];

  planTypes = PLAN_TYPES as readonly string[];

  constructor(private planService: PlanService, private fb: FormBuilder) {
    this.planForm = this.fb.group({
      planName: ['', Validators.required],
      planType: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      validityDays: [1, [Validators.required, Validators.min(1)]],
      hourlyLimit: [0, [Validators.required, Validators.min(0)]],
      dailyLimit: [0, [Validators.required, Validators.min(0)]],
      monthlyLimit: [0, [Validators.required, Validators.min(0)]],
      floorAccessAllowed: [true],
      seatAccessAllowed: [true],
      discountPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    });

    this.searchPlanForm = this.fb.group({
      planName: ['', Validators.required]
    });

    this.updateForm = this.fb.group({
      planName: ['', Validators.required],
      planType: ['', Validators.required],
      description: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      validityDays: [1, [Validators.required, Validators.min(1)]],
      hourlyLimit: [0, [Validators.required, Validators.min(0)]],
      dailyLimit: [0, [Validators.required, Validators.min(0)]],
      monthlyLimit: [0, [Validators.required, Validators.min(0)]],
      floorAccessAllowed: [true],
      seatAccessAllowed: [true],
      discountPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    });
  }

  ngOnInit() {
    this.fetchPlans();
  }

  fetchPlans() {
    this.planService.fetchPlans().subscribe({
      next: plans => {
        this.plans.set(plans);
        this.loading.set(false);
      },
      error: err => {
        console.error('Failed to load plans', err);
        this.plans.set([]);
        this.loading.set(false);
      }
    });
  }

  onActionChange(event: MatButtonToggleChange) {
    this.selectedAction = event.value;
    this.submissionMessage.set(null);
    this.updateMessage.set(null);
    this.searchError.set(null);
    this.selectedPlan.set(null);
    this.searchPlanForm.reset({ planName: '' });
  }

  getPlanStatus(plan: PlanModel): string {
    return plan.isActive ? 'Active' : 'Inactive';
  }

  searchPlanByName() {
    if (this.searchPlanForm.invalid) {
      this.searchPlanForm.markAllAsTouched();
      return;
    }

    this.isUpdateLoading.set(true);
    this.searchError.set(null);
    this.updateMessage.set(null);
    this.selectedPlan.set(null);

    const planName = this.searchPlanForm.value.planName.trim().toLowerCase();
    this.planService.fetchPlans()
      .pipe(finalize(() => this.isUpdateLoading.set(false)))
      .subscribe({
        next: plans => {
          const found = plans.find(plan => plan.planName.toLowerCase() === planName);
          if (!found) {
            this.searchError.set('Plan not found. Please verify the plan name and try again.');
            return;
          }

          this.selectedPlan.set(found);
          this.updateForm.reset({
            planName: found.planName,
            planType: found.planType,
            description: found.description,
            price: found.price,
            validityDays: found.validityDays,
            hourlyLimit: found.hourlyLimit,
            dailyLimit: found.dailyLimit,
            monthlyLimit: found.monthlyLimit,
            floorAccessAllowed: found.floorAccessAllowed,
            seatAccessAllowed: found.seatAccessAllowed,
            discountPercentage: found.discountPercentage,
          });
        },
        error: err => {
          console.error('Failed to search plan by name', err);
          this.searchError.set('Unable to search plans right now. Please try again later.');
        }
      });
  }

  onUpdatePlan() {
    if (this.updateForm.invalid) {
      this.updateForm.markAllAsTouched();
      return;
    }

    const selected = this.selectedPlan();
    if (!selected) {
      this.searchError.set('Please search for a plan before updating.');
      return;
    }

    this.isUpdateLoading.set(true);
    this.updateMessage.set(null);

    const raw = this.updateForm.value;
    const payload: PlanCreatePayload = {
      planName: raw.planName,
      planType: raw.planType,
      description: raw.description,
      price: Number(raw.price),
      validityDays: Number(raw.validityDays),
      hourlyLimit: Number(raw.hourlyLimit),
      dailyLimit: Number(raw.dailyLimit),
      monthlyLimit: Number(raw.monthlyLimit),
      floorAccessAllowed: raw.floorAccessAllowed,
      seatAccessAllowed: raw.seatAccessAllowed,
      discountPercentage: Number(raw.discountPercentage),
    };

    this.planService.updatePlan(selected.id, payload)
      .pipe(finalize(() => this.isUpdateLoading.set(false)))
      .subscribe({
        next: updated => {
          this.selectedPlan.set(updated);
          this.updateMessage.set('Plan updated successfully.');
          const current = this.plans();
          this.plans.set(current.map(item => item.id === updated.id ? updated : item));
        },
        error: err => {
          console.error('Failed to update plan', err);
          this.updateMessage.set('Failed to update plan. Please try again.');
        }
      });
  }

  onCreatePlan() {
    if (this.planForm.invalid) {
      this.planForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.submissionMessage.set(null);

    const raw = this.planForm.value;
    const payload: PlanCreatePayload = {
      planName: raw.planName,
      planType: raw.planType,
      description: raw.description,
      price: Number(raw.price),
      validityDays: Number(raw.validityDays),
      hourlyLimit: Number(raw.hourlyLimit),
      dailyLimit: Number(raw.dailyLimit),
      monthlyLimit: Number(raw.monthlyLimit),
      floorAccessAllowed: raw.floorAccessAllowed,
      seatAccessAllowed: raw.seatAccessAllowed,
      discountPercentage: Number(raw.discountPercentage),
    };

    this.planService.createPlan(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: created => {
          const current = this.plans();
          this.plans.set([created, ...current]);
          this.submissionMessage.set('Plan created successfully.');
          this.planForm.reset({
            planName: '',
            planType: '',
            description: '',
            price: 0,
            validityDays: 1,
            hourlyLimit: 0,
            dailyLimit: 0,
            monthlyLimit: 0,
            floorAccessAllowed: true,
            seatAccessAllowed: true,
            discountPercentage: 0,
          });
          this.selectedAction = 'view';
        },
        error: err => {
          console.error('Failed to create plan', err);
          this.submissionMessage.set('Failed to create plan. Please check the values and try again.');
        }
      });
  }
}
