import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, UserSummary } from '../admin';

@Component({
  selector: 'app-user-management',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.html',
  styleUrl: './user-management.scss'
})
export class UserManagement implements OnInit {
  users: UserSummary[] = [];
  filteredUsers: UserSummary[] = [];
  searchTerm = '';
  selectedRole = '';
  isLoading = true;
  errorMessage = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.errorMessage = 'Failed to load users';
        this.isLoading = false;
      }
    });
  }

  searchUsers() {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = this.users;
      return;
    }

    this.adminService.searchUsers(this.searchTerm).subscribe({
      next: (users) => {
        this.filteredUsers = users;
      },
      error: (error) => {
        console.error('Error searching users:', error);
        this.errorMessage = 'Failed to search users';
      }
    });
  }

  filterByRole() {
    if (!this.selectedRole) {
      this.filteredUsers = this.users;
      return;
    }

    this.adminService.getUsersByRole(this.selectedRole).subscribe({
      next: (users) => {
        this.filteredUsers = users;
      },
      error: (error) => {
        console.error('Error filtering users:', error);
        this.errorMessage = 'Failed to filter users';
      }
    });
  }

  deleteUser(userId: number) {
    const user = this.users.find(u => u.id === userId);
    if (!user) return;

    if (user.role === 'ADMIN') {
      // Only allow deletion for admin users
      if (confirm('Are you sure you want to delete this admin user? This action cannot be undone.')) {
        this.adminService.deleteUser(userId).subscribe({
          next: () => {
            this.loadUsers(); // Refresh the list
          },
          error: (error) => {
            console.error('Error deleting user:', error);
            this.errorMessage = error.error?.error || 'Failed to delete user';
          }
        });
      }
    } else {
      // For non-admin users, offer suspension
      this.suspendUser(userId);
    }
  }

  suspendUser(userId: number) {
    const user = this.users.find(u => u.id === userId);
    if (!user) return;

    if (user.isSuspended) {
      this.errorMessage = 'User is already suspended';
      return;
    }

    const reason = prompt('Enter reason for suspension (optional):') || 'No reason provided';
    if (confirm(`Are you sure you want to suspend user "${user.name}"?`)) {
      this.adminService.suspendUser(userId, reason).subscribe({
        next: () => {
          this.loadUsers(); // Refresh the list
        },
        error: (error) => {
          console.error('Error suspending user:', error);
          this.errorMessage = error.error?.error || 'Failed to suspend user';
        }
      });
    }
  }

  reactivateUser(userId: number) {
    const user = this.users.find(u => u.id === userId);
    if (!user) return;

    if (!user.isSuspended) {
      this.errorMessage = 'User is not suspended';
      return;
    }

    if (confirm(`Are you sure you want to reactivate user "${user.name}"?`)) {
      this.adminService.reactivateUser(userId).subscribe({
        next: () => {
          this.loadUsers(); // Refresh the list
        },
        error: (error) => {
          console.error('Error reactivating user:', error);
          this.errorMessage = error.error?.error || 'Failed to reactivate user';
        }
      });
    }
  }

  clearFilters() {
    this.searchTerm = '';
    this.selectedRole = '';
    this.filteredUsers = this.users;
  }

  isLastAdmin(user: UserSummary): boolean {
    if (user.role !== 'ADMIN') return false;
    const adminCount = this.users.filter(u => u.role === 'ADMIN').length;
    return adminCount <= 1;
  }
}
