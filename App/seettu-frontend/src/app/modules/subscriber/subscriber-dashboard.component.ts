import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SeettuService, SeettuGroup, Notification } from '../../services/seettu.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-subscriber-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subscriber-dashboard.component.html',
  styleUrls: ['./subscriber-dashboard.component.scss']
})
export class SubscriberDashboardComponent implements OnInit {
  myGroups: SeettuGroup[] = [];
  notifications: Notification[] = [];
  unreadCount = 0;
  showNotifications = false;

  constructor(
    private seettuService: SeettuService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    console.log('SubscriberDashboard initialized');
    console.log('Authentication status:', this.authService.isLoggedIn());
    console.log('User token:', !!this.authService.getToken());
    
    if (!this.authService.isLoggedIn()) {
      console.error('User not authenticated, redirecting to login');
      this.router.navigate(['/auth/login']);
      return;
    }
    
    this.loadMyGroups();
    this.loadNotifications();
    this.loadUnreadCount();
  }

  loadMyGroups() {
    this.seettuService.getMyGroups().subscribe({
      next: (groups) => {
        this.myGroups = groups;
      },
      error: (error) => {
        console.error('Error loading my groups:', error);
      }
    });
  }

  loadNotifications() {
    this.seettuService.getNotifications().subscribe({
      next: (notifications) => {
        console.log('Notifications loaded successfully:', notifications);
        this.notifications = notifications;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url,
          ok: error.ok,
          headers: error.headers?.keys(),
          body: error.error
        });
      }
    });
  }

  loadUnreadCount() {
    this.seettuService.getUnreadNotificationCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (error) => {
        console.error('Error loading unread count:', error);
      }
    });
  }

  loadAllNotifications() {
    // Toggle notification display and show all notifications
    this.showNotifications = !this.showNotifications;
    this.loadNotifications();
  }

  viewGroupDetails(groupId: number) {
    this.router.navigate(['/subscriber/groups', groupId]);
  }

  markAsRead(notificationId: number) {
    this.seettuService.markNotificationAsRead(notificationId).subscribe({
      next: () => {
        // Update the notification in the list
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification) {
          notification.isRead = true;
        }
        this.loadUnreadCount(); // Refresh unread count
      },
      error: (error) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  getMemberInfo(group: SeettuGroup): any {
    // This would need to be implemented based on how member info is stored
    // For now, return a placeholder
    return {
      orderNumber: 1, // This should come from the actual member data
      packageReceiveDate: new Date(group.startDate) // This should be calculated
    };
  }

  getCurrentMonth(group: SeettuGroup): number {
    if (!group.isStarted) return 0;
    
    const startDate = new Date(group.startDate);
    const currentDate = new Date();
    const monthsDiff = (currentDate.getFullYear() - startDate.getFullYear()) * 12 + 
                      (currentDate.getMonth() - startDate.getMonth()) + 1;
    
    return Math.min(monthsDiff, group.numberOfMonths);
  }

  getProgressPercentage(group: SeettuGroup): number {
    if (!group.isStarted) return 0;
    
    const currentMonth = this.getCurrentMonth(group);
    return (currentMonth / group.numberOfMonths) * 100;
  }
}