import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SeettuService } from '../../services/seettu.service';

@Component({
  selector: 'app-provider-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './provider-dashboard.component.html',
  styleUrls: ['./provider-dashboard.component.scss']
})
export class ProviderDashboardComponent implements OnInit {
  totalGroups = 0;
  activeGroups = 0;
  totalPackages = 0;
  allGroups: any[] = [];
  filteredGroups: any[] = [];
  selectedStatus: string = 'ALL';

  constructor(private seettuService: SeettuService) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    // Load groups
    this.seettuService.getGroups().subscribe({
      next: (groups) => {
        this.allGroups = groups;
        this.filteredGroups = groups;
        this.totalGroups = groups.length;
        this.activeGroups = groups.filter(g => g.status === 'ACTIVE').length;
        this.filterGroups(this.selectedStatus);
      },
      error: (error) => {
        console.error('Error loading groups:', error);
      }
    });

    // Load packages
    this.seettuService.getPackages().subscribe({
      next: (packages) => {
        this.totalPackages = packages.length;
      },
      error: (error) => {
        console.error('Error loading packages:', error);
      }
    });
  }

  filterGroups(status: string) {
    this.selectedStatus = status;
    if (status === 'ALL') {
      this.filteredGroups = this.allGroups;
    } else {
      this.filteredGroups = this.allGroups.filter(group => group.status === status);
    }
  }

  getGroupCount(status: string): number {
    if (status === 'ALL') {
      return this.allGroups.length;
    }
    return this.allGroups.filter(group => group.status === status).length;
  }

  getStatusDisplay(status: string): string {
    switch (status) {
      case 'PENDING': return 'Pending';
      case 'ACTIVE': return 'Active';
      case 'COMPLETED': return 'Completed';
      case 'CANCELLED': return 'Cancelled';
      default: return status || 'Unknown';
    }
  }

  startGroup(groupId: number) {
    if (confirm('Are you sure you want to start this group?')) {
      this.seettuService.startGroup(groupId).subscribe({
        next: () => {
          alert('Group started successfully!');
          this.loadDashboardData(); // Refresh data
        },
        error: (error) => {
          console.error('Error starting group:', error);
          alert('Error starting group. Please try again.');
        }
      });
    }
  }

  cancelGroup(groupId: number, groupName: string) {
    if (confirm(`Are you sure you want to cancel the group "${groupName}"? This action cannot be undone.`)) {
      this.seettuService.cancelGroup(groupId).subscribe({
        next: () => {
          alert('Group cancelled successfully!');
          this.loadDashboardData(); // Refresh data
        },
        error: (error) => {
          console.error('Error cancelling group:', error);
          alert('Error cancelling group. Please try again.');
        }
      });
    }
  }
}