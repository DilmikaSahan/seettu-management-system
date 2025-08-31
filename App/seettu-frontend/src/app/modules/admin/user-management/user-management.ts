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
    if (confirm('Are you sure you want to delete this user?')) {
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
