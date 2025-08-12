import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SeettuService, SubscriberGroupDetails, PaymentStatus } from '../../services/seettu.service';

@Component({
  selector: 'app-subscriber-group-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subscriber-group-details.component.html',
  styleUrls: ['./subscriber-group-details.component.scss']
})
export class SubscriberGroupDetailsComponent implements OnInit {
  groupDetails?: SubscriberGroupDetails;
  loading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private seettuService: SeettuService
  ) {}

  ngOnInit() {
    const groupId = this.route.snapshot.params['id'];
    if (groupId) {
      this.loadGroupDetails(+groupId);
    }
  }

  loadGroupDetails(groupId: number) {
    this.loading = true;
    this.seettuService.getGroupDetailsForSubscriber(groupId).subscribe({
      next: (details) => {
        // Convert date strings to proper Date objects
        if (details.payments) {
          details.payments = details.payments.map(payment => ({
            ...payment,
            paidAt: payment.paidAt ? this.convertDateString(payment.paidAt.toString()) : undefined,
            paymentDate: this.convertDateString(payment.paymentDate) || payment.paymentDate
          }));
        }
        this.groupDetails = details;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading group details:', error);
        this.error = 'Failed to load group details';
        this.loading = false;
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

  getPaymentStatusClass(payment: PaymentStatus): string {
    switch (payment.status) {
      case 'PAID': return 'status-paid';
      case 'OVERDUE': return 'status-overdue';
      case 'PENDING': return 'status-pending';
      default: return '';
    }
  }

  getStatusText(payment: PaymentStatus): string {
    switch (payment.status) {
      case 'PAID': return 'Paid';
      case 'OVERDUE': return 'Overdue';
      case 'PENDING': return 'Pending';
      default: return payment.status;
    }
  }

  isCurrentMonth(payment: PaymentStatus): boolean {
    if (!this.groupDetails) return false;
    return payment.monthNumber === this.groupDetails.currentMonth;
  }

  goBack() {
    this.router.navigate(['/subscriber/dashboard']);
  }
}
