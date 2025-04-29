import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { User } from '../../models/auth.model';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h3>User Management</h3>
              <button class="btn btn-primary" (click)="showAddUserForm = !showAddUserForm">
                {{ showAddUserForm ? 'Hide Form' : 'Add New User' }}
              </button>
            </div>
            
            <div class="card-body" *ngIf="showAddUserForm">
              <h4>Create New User</h4>
              <form [formGroup]="userForm" (ngSubmit)="onSubmit()">
                <div class="alert alert-danger" *ngIf="error">{{ error }}</div>
                <div class="alert alert-success" *ngIf="success">{{ success }}</div>
                
                <div class="row">
                  <div class="col-md-6 mb-3">
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
                  
                  <div class="col-md-6 mb-3">
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
                </div>
                
                <div class="row">
                  <div class="col-md-6 mb-3">
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
                    </div>
                  </div>
                  
                  <div class="col-md-6 mb-3">
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
                    </div>
                  </div>
                </div>
                
                <div class="mb-3">
                  <label for="role" class="form-label">Role</label>
                  <select 
                    class="form-select" 
                    id="role" 
                    formControlName="role"
                    [ngClass]="{'is-invalid': submitted && f['role'].errors}"
                  >
                    <option value="">Select Role</option>
                    <option value="ADMIN">Administrator</option>
                    <option value="MANAGER">Manager</option>
                    <option value="USER">Standard User</option>
                  </select>
                  <div *ngIf="submitted && f['role'].errors" class="invalid-feedback">
                    <div *ngIf="f['role'].errors['required']">Role is required</div>
                  </div>
                </div>
                
                <div class="d-grid gap-2">
                  <button type="submit" class="btn btn-success" [disabled]="loading">
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-1"></span>
                    Save User
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
      
      <div class="row mt-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h3>User List</h3>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Username</th>
                      <th>Name</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let user of users">
                      <td>{{ user.id }}</td>
                      <td>{{ user.username }}</td>
                      <td>{{ user.firstName }} {{ user.lastName }}</td>
                      <td>
                        <span class="badge bg-primary">{{ user.role }}</span>
                      </td>
                      <td>
                        <span class="badge" [ngClass]="user.active ? 'bg-success' : 'bg-danger'">
                          {{ user.active ? 'Active' : 'Inactive' }}
                        </span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-warning me-1" (click)="editUser(user)">Edit</button>
                        <button 
                          class="btn btn-sm" 
                          [ngClass]="user.active ? 'btn-danger' : 'btn-success'"
                          (click)="toggleUserStatus(user)"
                        >
                          {{ user.active ? 'Deactivate' : 'Activate' }}
                        </button>
                      </td>
                    </tr>
                    <tr *ngIf="users.length === 0">
                      <td colspan="6" class="text-center">No users found</td>
                    </tr>
                  </tbody>
                </table>
              </div>
              
              <div *ngIf="loading" class="d-flex justify-content-center mt-3">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  userForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';
  success = '';
  showAddUserForm = false;
  editMode = false;
  currentUserId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {
    this.userForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', Validators.required],
      password: ['', this.editMode ? [] : Validators.required],
      role: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  // Getter for easy access to form fields
  get f() { return this.userForm.controls; }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load users: ' + (error.message || 'Unknown error');
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    
    // Stop here if form is invalid
    if (this.userForm.invalid) {
      return;
    }
    
    this.loading = true;
    
    if (this.editMode && this.currentUserId) {
      this.userService.updateUser(this.currentUserId, this.userForm.value).subscribe({
        next: () => {
          this.success = 'User updated successfully';
          this.resetForm();
          this.loadUsers();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to update user';
          this.loading = false;
        }
      });
    } else {
      this.userService.createUser(this.userForm.value).subscribe({
        next: () => {
          this.success = 'User created successfully';
          this.resetForm();
          this.loadUsers();
        },
        error: (error) => {
          this.error = error.error?.message || 'Failed to create user';
          this.loading = false;
        }
      });
    }
  }

  editUser(user: User): void {
    this.editMode = true;
    this.currentUserId = user.id;
    this.showAddUserForm = true;
    
    this.userForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      username: user.username,
      role: user.role
    });
    
    // Make password optional in edit mode
    this.userForm.get('password')?.clearValidators();
    this.userForm.get('password')?.updateValueAndValidity();
  }

  toggleUserStatus(user: User): void {
    this.loading = true;
    this.userService.toggleUserStatus(user.id).subscribe({
      next: () => {
        user.active = !user.active;
        this.success = `User ${user.active ? 'activated' : 'deactivated'} successfully`;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message || 'Failed to update user status';
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.userForm.reset();
    this.submitted = false;
    this.loading = false;
    this.editMode = false;
    this.currentUserId = null;
    
    // Reset password validator for new user
    this.userForm.get('password')?.setValidators([Validators.required]);
    this.userForm.get('password')?.updateValueAndValidity();
    
    // Clear messages after 5 seconds
    setTimeout(() => {
      this.success = '';
      this.error = '';
    }, 5000);
  }
} 