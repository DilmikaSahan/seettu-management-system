import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SeettuService, GroupDetailsResponse } from '../../services/seettu.service';

@Component({
  selector: 'app-group-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './group-details.component.html',
  styleUrls: ['./group-details.component.scss']
})
export class GroupDetailsComponent implements OnInit {
  groupDetails: GroupDetailsResponse | null = null;
  error: string | null = null;
  groupId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private seettuService: SeettuService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.groupId = +params['id'];
      this.loadGroupDetails();
    });
  }

  loadGroupDetails() {
    this.error = null;
    this.seettuService.getGroupDetails(this.groupId).subscribe({
      next: (details) => {
        // Convert date strings to proper Date objects
        if (details.monthlyPayments) {
          details.monthlyPayments = details.monthlyPayments.map(month => ({
            ...month,
            paymentDate: this.convertDateString(month.paymentDate) || month.paymentDate
          }));
        }
        this.groupDetails = details;
      },
      error: (error) => {
        console.error('Error loading group details:', error);
        this.error = 'Failed to load group details. Please try again.';
      }
    });
  }

  convertDateString(dateStr: string): string | undefined {
    if (!dateStr) return undefined;
    
    try {
      // Handle array format like "2025,8,12,11,54,50,714308000"
      if (dateStr.includes(',')) {
        const parts = dateStr.split(',').map(Number);
        if (parts.length >= 6) {
          // Convert to JavaScript Date (month is 0-indexed)
          const date = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5], Math.floor(parts[6] / 1000000));
          return date.toISOString();
        }
      }
      
      // If it's already a proper date string, return it
      return dateStr;
    } catch (error) {
      console.error('Error converting date:', dateStr, error);
      return undefined;
    }
  }

  startGroup() {
    if (confirm('Are you sure you want to start this group? This action cannot be undone.')) {
      this.seettuService.startGroup(this.groupId).subscribe({
        next: () => {
          alert('Group started successfully!');
          this.loadGroupDetails(); // Refresh data
        },
        error: (error) => {
          console.error('Error starting group:', error);
          alert('Error starting group. Please try again.');
        }
      });
    }
  }

  markAsPaid(paymentId: number) {
    if (confirm('Mark this payment as received?')) {
      this.seettuService.markPaymentAsPaid(paymentId).subscribe({
        next: () => {
          alert('Payment marked as paid! Notification sent to member.');
          this.loadGroupDetails(); // Refresh data
        },
        error: (error) => {
          console.error('Error marking payment as paid:', error);
          alert('Error updating payment status. Please try again.');
        }
      });
    }
  }

  getPaidCount(memberPayments: any[]): number {
    return memberPayments.filter(p => p.isPaid).length;
  }

  goBack() {
    this.router.navigate(['/provider/dashboard']);
  }
}