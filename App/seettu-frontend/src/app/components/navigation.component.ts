import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../modules/auth/auth.service';
import { SeettuService } from '../services/seettu.service';
import { Subscription, timer } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="navbar" *ngIf="isLoggedIn">
      <div class="nav-container">
        <div class="nav-brand">
          <a routerLink="/" class="brand-link">Seettu Management</a>
        </div>
        
        <div class="nav-menu">
          <!-- Provider Navigation -->
          <div class="nav-section" *ngIf="userRole === 'PROVIDER'">
            <a routerLink="/provider/dashboard" routerLinkActive="active" class="nav-link">
              Dashboard
            </a>
            <a routerLink="/provider/packages/create" routerLinkActive="active" class="nav-link">
              Create Package
            </a>
            <a routerLink="/provider/groups/create" routerLinkActive="active" class="nav-link">
              Create Group
            </a>
            <a routerLink="/provider/subscribers/add" routerLinkActive="active" class="nav-link">
              Add Subscriber
            </a>
          </div>
          
          <!-- Subscriber Navigation -->
          <div class="nav-section" *ngIf="userRole === 'SUBSCRIBER'">
            <a routerLink="/subscriber/dashboard" routerLinkActive="active" class="nav-link">
              My Dashboard
            </a>
          </div>
          
          <!-- Admin Navigation -->
          <div class="nav-section" *ngIf="userRole === 'ADMIN'">
            <a routerLink="/admin/dashboard" routerLinkActive="active" class="nav-link">
              🔧 Admin Dashboard
            </a>
            <a routerLink="/admin/users" routerLinkActive="active" class="nav-link">
              👥 User Management
            </a>
          </div>
        </div>
        
        <div class="nav-actions">
          <!-- Notification bell icon - Only show for subscribers -->
          <div class="notification-container" *ngIf="isLoggedIn && userRole === 'SUBSCRIBER'">
            <button class="notification-btn" (click)="toggleNotifications()" [title]="getNotificationTooltip()">
              🔔
              <span class="notification-badge" *ngIf="unreadCount > 0">{{ unreadCount }}</span>
            </button>
            
            <!-- Notification Dropdown -->
            <div class="notification-dropdown" *ngIf="showNotifications">
              <div class="notification-header">
                <h4>Notifications</h4>
                <button class="close-btn" (click)="toggleNotifications()">×</button>
              </div>
              
              <div class="notification-list" *ngIf="notifications.length > 0; else noNotifications">
                <div class="notification-item" 
                     *ngFor="let notification of notifications" 
                     [class.unread]="!notification.isRead"
                     (click)="markAsRead(notification.id)">
                  <div class="notification-content">
                    <h5>{{ notification.title }}</h5>
                    <p>{{ notification.message }}</p>
                    <small>{{ notification.createdAt | date:'short' }}</small>
                  </div>
                </div>
              </div>
              
              <ng-template #noNotifications>
                <div class="no-notifications">
                  <p>No notifications yet</p>
                </div>
              </ng-template>
              
              <div class="notification-actions" *ngIf="unreadCount > 0">
                <button class="btn btn-sm" (click)="markAllAsRead()">
                  Mark all as read
                </button>
              </div>
            </div>
          </div>
          
          <!-- Login/User menu -->
          <div *ngIf="isLoggedIn" class="user-section">
            <span class="user-info">{{ userEmail }}</span>
            <button class="btn btn-outline" (click)="logout()">Logout</button>
          </div>
          
          <!-- Login buttons for non-logged in users -->
          <div *ngIf="!isLoggedIn" class="auth-buttons">
            <a routerLink="/auth/login" class="btn btn-outline">Login</a>
            <a routerLink="/auth/register" class="btn btn-primary">Register</a>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: #fff;
      border-bottom: 1px solid #e9ecef;
      padding: 0;
      position: sticky;
      top: 0;
      z-index: 1000;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      height: 60px;
    }

    .nav-brand .brand-link {
      font-size: 1.5rem;
      font-weight: bold;
      color: #007bff;
      text-decoration: none;
    }

    .nav-menu {
      display: flex;
      align-items: center;
      gap: 30px;
    }

    .nav-section {
      display: flex;
      gap: 20px;
    }

    .nav-link {
      color: #333;
      text-decoration: none;
      padding: 8px 12px;
      border-radius: 4px;
      transition: all 0.3s;
      font-weight: 500;
    }

    .nav-link:hover {
      background-color: #f8f9fa;
      color: #007bff;
    }

    .nav-link.active {
      background-color: #007bff;
      color: white;
    }

    .nav-actions {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .user-info {
      color: #666;
      font-size: 14px;
    }

    .btn {
      padding: 6px 12px;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s;
      text-decoration: none;
      border: 1px solid transparent;
    }

    .btn-outline {
      background: transparent;
      color: #007bff;
      border-color: #007bff;
    }

    .btn-outline:hover {
      background: #007bff;
      color: white;
    }

    /* Notification Styles */
    .notification-container {
      position: relative;
      display: inline-block;
    }

    .notification-btn {
      background: none;
      border: none;
      font-size: 1.2rem;
      cursor: pointer;
      padding: 8px;
      border-radius: 50%;
      transition: all 0.3s ease;
      position: relative;
    }

    .notification-btn:hover {
      background-color: #f8f9fa;
      transform: scale(1.05);
    }

    .notification-btn:active {
      background-color: #e9ecef;
      transform: scale(0.95);
    }

    .notification-btn.active {
      background-color: #007bff;
      color: white;
    }

    .notification-badge {
      position: absolute;
      top: 0;
      right: 0;
      background: #dc3545;
      color: white;
      border-radius: 50%;
      padding: 2px 6px;
      font-size: 0.75rem;
      font-weight: bold;
      min-width: 18px;
      text-align: center;
      line-height: 1;
    }

    /* Notification Dropdown Styles */
    .notification-dropdown {
      position: absolute;
      top: 100%;
      right: 0;
      background: white;
      border: 1px solid #ddd;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      width: 320px;
      max-height: 400px;
      z-index: 1000;
      overflow: hidden;
    }

    .notification-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid #eee;
      background: #f8f9fa;
    }

    .notification-header h4 {
      margin: 0;
      font-size: 1rem;
      color: #333;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: #666;
      padding: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .close-btn:hover {
      color: #333;
    }

    .notification-list {
      max-height: 280px;
      overflow-y: auto;
    }

    .notification-item {
      padding: 12px 16px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: background-color 0.2s;
    }

    .notification-item:hover {
      background-color: #f8f9fa;
    }

    .notification-item.unread {
      background-color: #e3f2fd;
      border-left: 4px solid #007bff;
    }

    .notification-content h5 {
      margin: 0 0 4px 0;
      font-size: 0.9rem;
      font-weight: 600;
      color: #333;
    }

    .notification-content p {
      margin: 0 0 4px 0;
      font-size: 0.85rem;
      color: #666;
      line-height: 1.4;
    }

    .notification-content small {
      color: #999;
      font-size: 0.75rem;
    }

    .no-notifications {
      padding: 20px;
      text-align: center;
      color: #666;
    }

    .notification-actions {
      padding: 12px 16px;
      border-top: 1px solid #eee;
      background: #f8f9fa;
      text-align: center;
    }

    /* User section styles */
    .user-section {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .auth-buttons {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .btn-sm {
      padding: 4px 12px;
      font-size: 0.85rem;
      background: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .btn-sm:hover {
      background: #0056b3;
    }

    @media (max-width: 768px) {
      .nav-container {
        flex-direction: column;
        height: auto;
        padding: 10px 20px;
        gap: 15px;
      }

      .nav-menu {
        width: 100%;
        justify-content: center;
      }

      .nav-section {
        flex-wrap: wrap;
        justify-content: center;
      }

      .nav-actions {
        width: 100%;
        justify-content: center;
      }
    }

  `]
})
export class NavigationComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  userRole: string | null = null;
  userEmail: string | null = null;
  showNotifications = false;
  notifications: any[] = [];
  unreadCount = 0;
  private routerSubscription: Subscription = new Subscription();
  private notificationSubscription: Subscription = new Subscription();

  constructor(
    private authService: AuthService,
    private router: Router,
    private seettuService: SeettuService,
    private elementRef: ElementRef
  ) {}

  ngOnInit() {
    this.checkAuthStatus();
    
    // Listen for route changes to refresh auth status
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.checkAuthStatus();
      });
    
    // Load notifications if logged in and user is a subscriber
    if (this.isLoggedIn && this.userRole === 'SUBSCRIBER') {
      this.loadNotifications();
      // Poll for new notifications every 30 seconds
      this.notificationSubscription = timer(0, 30000)
        .pipe(
          switchMap(() => this.seettuService.getUnreadNotificationCount())
        )
        .subscribe({
          next: (count) => {
            this.unreadCount = count;
          },
          error: (error) => {
            console.error('Error loading notification count:', error);
          }
        });
    }
  }

  ngOnDestroy() {
    this.routerSubscription.unsubscribe();
    this.notificationSubscription.unsubscribe();
  }

  checkAuthStatus() {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.userRole = this.authService.getUserRole();
      this.userEmail = this.authService.getUserEmail();
      // Only load notifications for subscribers
      if (this.userRole === 'SUBSCRIBER') {
        this.loadNotifications();
      }
    } else {
      this.userRole = null;
      this.userEmail = null;
      this.notifications = [];
      this.unreadCount = 0;
    }
  }

  loadNotifications() {
    if (!this.isLoggedIn || this.userRole !== 'SUBSCRIBER') return;
    
    // Load notifications
    this.seettuService.getUnreadNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
      }
    });
    
    // Load unread count
    this.seettuService.getUnreadNotificationCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
      },
      error: (error) => {
        console.error('Error loading notification count:', error);
      }
    });
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.loadNotifications();
    }
  }

  markAsRead(notificationId: number) {
    this.seettuService.markNotificationAsRead(notificationId).subscribe({
      next: () => {
        // Update local state
        const notification = this.notifications.find(n => n.id === notificationId);
        if (notification && !notification.isRead) {
          notification.isRead = true;
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      },
      error: (error) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  markAllAsRead() {
    // Mark all unread notifications as read
    const unreadNotifications = this.notifications.filter(n => !n.isRead);
    unreadNotifications.forEach(notification => {
      this.markAsRead(notification.id);
    });
  }

  getNotificationTooltip(): string {
    if (this.unreadCount > 0) {
      return `${this.unreadCount} unread notification${this.unreadCount > 1 ? 's' : ''}`;
    }
    return 'No new notifications';
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (this.showNotifications && !this.elementRef.nativeElement.contains(event.target)) {
      this.showNotifications = false;
    }
  }

  logout() {
    this.authService.logout();
    this.checkAuthStatus(); // Refresh immediately
    this.router.navigate(['/login']);
  }
}