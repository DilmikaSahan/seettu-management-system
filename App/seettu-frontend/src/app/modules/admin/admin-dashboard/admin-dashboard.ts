import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService, AdminDashboardStats, CreateAdminRequest } from '../admin';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss'
})
export class AdminDashboard implements OnInit {
  dashboardStats: AdminDashboardStats | null = null;
  showModal = false;
  isLoading = true;
  isCreating = false;
  errorMessage = '';

  newAdmin: CreateAdminRequest = {
    name: '',
    email: '',
    password: ''
  };

  constructor(
    private adminService: AdminService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadDashboardStats();
  }

  loadDashboardStats() {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.getDashboardStats().subscribe({
      next: (stats) => {
        this.dashboardStats = stats;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.errorMessage = 'Failed to load dashboard statistics';
        this.isLoading = false;
      }
    });
  }

  refreshStats() {
    this.loadDashboardStats();
  }

  navigateToUserManagement() {
    this.router.navigate(['/admin/users']);
  }

  showCreateAdminModal() {
    this.showModal = true;
    this.resetForm();
  }

  closeModal() {
    this.showModal = false;
    this.resetForm();
  }

  resetForm() {
    this.newAdmin = {
      name: '',
      email: '',
      password: ''
    };
    this.errorMessage = '';
  }

  createAdmin() {
    if (!this.validateForm()) {
      return;
    }

    this.isCreating = true;
    this.errorMessage = '';

    this.adminService.createAdmin(this.newAdmin).subscribe({
      next: (admin) => {
        console.log('Admin created successfully:', admin);
        this.closeModal();
        this.loadDashboardStats(); // Refresh stats
        this.isCreating = false;
        // You might want to show a success message here
      },
      error: (error) => {
        console.error('Error creating admin:', error);
        this.errorMessage = error.error?.error || 'Failed to create administrator';
        this.isCreating = false;
      }
    });
  }

  private validateForm(): boolean {
    if (!this.newAdmin.name.trim()) {
      this.errorMessage = 'Name is required';
      return false;
    }
    if (!this.newAdmin.email.trim() || !this.newAdmin.email.includes('@')) {
      this.errorMessage = 'Valid email is required';
      return false;
    }
    if (!this.newAdmin.password || this.newAdmin.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return false;
    }
    return true;
  }
}
