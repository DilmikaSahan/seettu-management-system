import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SeettuService, SeettuPackage, User, CreateGroupRequest, MemberRequest } from '../../services/seettu.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-create-group',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-group.component.html',
  styleUrls: ['./create-group.component.scss']
})
export class CreateGroupComponent implements OnInit {
  groupData: CreateGroupRequest = {
    groupName: '',
    packageId: 0,
    monthlyAmount: 0,
    numberOfMonths: 12,
    startDate: '',
    members: []
  };

  packages: SeettuPackage[] = [];
  searchTerm = '';
  searchResults: User[] = [];
  selectedUsers: User[] = [];
  isSubmitting = false;

  constructor(
    private seettuService: SeettuService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      alert('Please login to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    // Check if user has PROVIDER role
    const userRole = this.authService.getUserRole();
    if (userRole !== 'PROVIDER') {
      alert('Access denied. Only providers can create groups.');
      this.router.navigate(['/login']);
      return;
    }

    console.log('User authenticated:', this.authService.getUserEmail());
    console.log('User role:', userRole);
    
    this.loadPackages();
    // Set default start date to today
    const today = new Date();
    this.groupData.startDate = today.toISOString().split('T')[0];
  }

  loadPackages() {
    this.seettuService.getPackages().subscribe({
      next: (packages) => {
        this.packages = packages;
      },
      error: (error) => {
        console.error('Error loading packages:', error);
      }
    });
  }

  searchSubscribers() {
    if (this.searchTerm.trim().length < 2) {
      this.searchResults = [];
      return;
    }

    this.seettuService.searchSubscribers(this.searchTerm).subscribe({
      next: (users) => {
        this.searchResults = users;
      },
      error: (error) => {
        console.error('Error searching subscribers:', error);
      }
    });
  }

  addMember(user: User) {
    if (this.isMemberAdded(user.id!)) return;

    this.selectedUsers.push(user);
    
    const memberRequest: MemberRequest = {
      userId: user.id!,
      orderNumber: this.groupData.members.length + 1
    };

    this.groupData.members.push(memberRequest);
    this.searchResults = [];
    this.searchTerm = '';
  }

  removeMember(index: number) {
    const removedMember = this.groupData.members[index];
    this.groupData.members.splice(index, 1);
    
    // Remove from selected users
    this.selectedUsers = this.selectedUsers.filter(u => u.id !== removedMember.userId);
    
    // Reorder remaining members
    this.groupData.members.forEach((member, i) => {
      if (member.orderNumber > removedMember.orderNumber) {
        member.orderNumber = i + 1;
      }
    });
  }

  isMemberAdded(userId: number): boolean {
    return this.groupData.members.some(m => m.userId === userId);
  }

  getMemberName(userId: number): string {
    const user = this.selectedUsers.find(u => u.id === userId);
    return user ? user.name : 'Unknown User';
  }

  onMonthsChange() {
    // Ensure no member has an order number greater than the number of months
    this.groupData.members.forEach(member => {
      if (member.orderNumber > this.groupData.numberOfMonths) {
        member.orderNumber = this.groupData.numberOfMonths;
      }
    });
  }

  onSubmit() {
    if (this.isSubmitting) return;

    // Validate order numbers are unique
    const orderNumbers = this.groupData.members.map(m => m.orderNumber);
    const uniqueOrderNumbers = new Set(orderNumbers);
    
    if (orderNumbers.length !== uniqueOrderNumbers.size) {
      alert('Each member must have a unique order number.');
      return;
    }

    this.isSubmitting = true;

    this.seettuService.createGroup(this.groupData).subscribe({
      next: (response) => {
        alert('Group created successfully!');
        this.router.navigate(['/provider/dashboard']);
      },
      error: (error) => {
        console.error('Error creating group:', error);
        
        if (error.status === 403) {
          alert('Authentication failed. Please login again.');
          this.router.navigate(['/login']);
        } else if (error.status === 401) {
          alert('Invalid or expired token. Please login again.');
          this.router.navigate(['/login']);
        } else {
          alert(`Error creating group: ${error.message || 'Please try again.'}`);
        }
        
        this.isSubmitting = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/provider/dashboard']);
  }
}