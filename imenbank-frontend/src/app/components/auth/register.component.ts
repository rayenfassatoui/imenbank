import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="register-container">
      <div class="card">
        <div class="card-header">
          <h2>Register for ImenBank</h2>
        </div>
        <div class="card-body">
          <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
            <div class="alert alert-danger" *ngIf="error">
              {{ error }}
            </div>
            
            <div class="mb-3">
              <label for="firstName" class="form-label">First Name</label>
              <input 
                type="text" 
                class="form-control" 
                id="firstName" 
                formControlName="firstName"
                [ngClass]="{'is-invalid': submitted && f['firstName'].errors}"
              >
              <div *ngIf="submitted && f['firstName'].errors" class="invalid-feedback">
                <div *ngIf="f['firstName'].errors['required']">First name is required</div>
              </div>
            </div>
            
            <div class="mb-3">
              <label for="lastName" class="form-label">Last Name</label>
              <input 
                type="text" 
                class="form-control" 
                id="lastName" 
                formControlName="lastName"
                [ngClass]="{'is-invalid': submitted && f['lastName'].errors}"
              >
              <div *ngIf="submitted && f['lastName'].errors" class="invalid-feedback">
                <div *ngIf="f['lastName'].errors['required']">Last name is required</div>
              </div>
            </div>
            
            <div class="mb-3">
              <label for="username" class="form-label">Username</label>
              <input 
                type="text" 
                class="form-control" 
                id="username" 
                formControlName="username"
                [ngClass]="{'is-invalid': submitted && f['username'].errors}"
              >
              <div *ngIf="submitted && f['username'].errors" class="invalid-feedback">
                <div *ngIf="f['username'].errors['required']">Username is required</div>
                <div *ngIf="f['username'].errors['minlength']">Username must be at least 3 characters</div>
              </div>
            </div>
            
            <div class="mb-3">
              <label for="password" class="form-label">Password</label>
              <input 
                type="password" 
                class="form-control" 
                id="password" 
                formControlName="password"
                [ngClass]="{'is-invalid': submitted && f['password'].errors}"
              >
              <div *ngIf="submitted && f['password'].errors" class="invalid-feedback">
                <div *ngIf="f['password'].errors['required']">Password is required</div>
                <div *ngIf="f['password'].errors['minlength']">Password must be at least 6 characters</div>
              </div>
            </div>
            
            <div class="d-grid gap-2">
              <button type="submit" class="btn btn-primary" [disabled]="loading">
                <span *ngIf="loading" class="spinner-border spinner-border-sm me-1"></span>
                Register
              </button>
            </div>
            
            <div class="mt-3 text-center">
              <p>Already have an account? <a routerLink="/login">Login</a></p>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background-color: #f5f5f5;
    }
    
    .card {
      width: 100%;
      max-width: 400px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    
    .card-header {
      background-color: #3f51b5;
      color: white;
      padding: 1rem;
      text-align: center;
    }
  `]
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    // Redirect to home if already logged in
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
    
    this.registerForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {}
  
  // Getter for easy access to form fields
  get f() { return this.registerForm.controls; }
  
  onSubmit(): void {
    this.submitted = true;
    
    // Stop here if form is invalid
    if (this.registerForm.invalid) {
      return;
    }
    
    this.loading = true;
    const registerRequest: RegisterRequest = {
      firstName: this.f['firstName'].value,
      lastName: this.f['lastName'].value,
      username: this.f['username'].value,
      password: this.f['password'].value
    };
    
    this.authService.register(registerRequest)
      .subscribe({
        next: () => {
          this.router.navigate(['/login'], { queryParams: { registered: true } });
        },
        error: error => {
          this.error = error.error?.message || 'Registration failed. Please try again.';
          this.loading = false;
        }
      });
  }
} 