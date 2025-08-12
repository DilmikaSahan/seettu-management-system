import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../modules/auth/auth.service';

export interface SeettuPackage {
  id?: number;
  packageName: string;
  description: string;
  packageValue: number;
  provider?: any;
  isActive?: boolean;
}

export interface CreateGroupRequest {
  groupName: string;
  packageId: number;
  monthlyAmount: number;
  numberOfMonths: number;
  startDate: string;
  members: MemberRequest[];
}

export interface MemberRequest {
  userId: number;
  orderNumber: number;
}

export interface SeettuGroup {
  id?: number;
  groupName: string;
  provider?: any;
  seettuPackage?: SeettuPackage;
  monthlyAmount: number;
  numberOfMonths: number;
  startDate: string;
  isActive: boolean;
  isStarted: boolean;
  status?: string;
  memberCount?: number;
  members?: any[];
  payments?: any[];
}

export interface GroupDetailsResponse {
  id: number;
  groupName: string;
  packageName: string;
  monthlyAmount: number;
  numberOfMonths: number;
  startDate: string;
  isActive: boolean;
  isStarted: boolean;
  members: MemberDetails[];
  monthlyPayments: MonthlyPaymentStatus[];
}

export interface MemberDetails {
  id: number;
  name: string;
  phoneNumber: string;
  orderNumber: number;
  packageReceiveDate: string;
}

export interface MonthlyPaymentStatus {
  monthNumber: number;
  paymentDate: string;
  memberPayments: MemberPaymentStatus[];
}

export interface MemberPaymentStatus {
  memberId: number;
  memberName: string;
  isPaid: boolean;
  paymentId?: number;
}

export interface User {
  id?: number;
  name: string;
  email: string;
  phoneNumber?: string;
  userId?: string;
  role?: string;
}

export interface AddSubscriberRequest {
  name: string;
  email: string;
  phoneNumber: string;
  password: string;
  // userId will be auto-generated on the backend
}

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
  groupId?: number;
  groupName?: string;
  paymentId?: number;
}

export interface PaymentStatus {
  paymentId: number;
  monthNumber: number;
  amount: number;
  paymentDate: string;
  isPaid: boolean;
  paidAt?: string | Date;
  isOverdue: boolean;
  status: 'PAID' | 'PENDING' | 'OVERDUE';
}

export interface SubscriberGroupDetails {
  groupId: number;
  groupName: string;
  packageName: string;
  monthlyAmount: number;
  numberOfMonths: number;
  startDate: string;
  isActive: boolean;
  isStarted: boolean;
  myOrderNumber: number;
  myPackageReceiveDate: string;
  hasReceivedPackage: boolean;
  payments: PaymentStatus[];
  totalPaidMonths: number;
  totalOverdueMonths: number;
  totalPaidAmount: number;
  totalOwedAmount: number;
  currentMonth: number;
  progressPercentage: number;
}

@Injectable({
  providedIn: 'root'
})
export class SeettuService {
  private apiUrl = 'http://localhost:8080/api/seettu';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    console.log('Token found in localStorage:', !!token);
    console.log('Token value:', token ? token.substring(0, 20) + '...' : 'null');
    
    if (!token) {
      console.error('No JWT token found! User might not be logged in.');
      // Return basic headers instead of throwing error
      return new HttpHeaders({
        'Content-Type': 'application/json'
      });
    }
    
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Package methods
  createPackage(packageData: Omit<SeettuPackage, 'id'>): Observable<SeettuPackage> {
    return this.http.post<SeettuPackage>(`${this.apiUrl}/packages`, packageData, {
      headers: this.getHeaders()
    });
  }

  getPackages(): Observable<SeettuPackage[]> {
    return this.http.get<SeettuPackage[]>(`${this.apiUrl}/packages`, {
      headers: this.getHeaders()
    });
  }

  getAllPackages(): Observable<SeettuPackage[]> {
    return this.http.get<SeettuPackage[]>(`${this.apiUrl}/packages/all`, {
      headers: this.getHeaders()
    });
  }

  deletePackage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/packages/${id}`, {
      headers: this.getHeaders()
    });
  }

  // Group methods
  createGroup(groupData: CreateGroupRequest): Observable<SeettuGroup> {
    return this.http.post<SeettuGroup>(`${this.apiUrl}/groups`, groupData, {
      headers: this.getHeaders()
    });
  }

  getGroups(): Observable<SeettuGroup[]> {
    return this.http.get<SeettuGroup[]>(`${this.apiUrl}/groups`, {
      headers: this.getHeaders()
    });
  }

  getGroupDetails(id: number): Observable<GroupDetailsResponse> {
    return this.http.get<GroupDetailsResponse>(`${this.apiUrl}/groups/${id}`, {
      headers: this.getHeaders()
    });
  }

  startGroup(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/groups/${id}/start`, {}, {
      headers: this.getHeaders()
    });
  }

  markPaymentAsPaid(paymentId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/payments/${paymentId}/mark-paid`, {}, {
      headers: this.getHeaders()
    });
  }

  // Subscriber methods
  addSubscriber(subscriberData: AddSubscriberRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/subscribers`, subscriberData, {
      headers: this.getHeaders()
    });
  }

  searchSubscribers(term: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/subscribers/search?term=${term}`, {
      headers: this.getHeaders()
    });
  }

  getAllSubscribers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/subscribers`, {
      headers: this.getHeaders()
    });
  }

  // Subscriber dashboard methods
  getMyGroups(): Observable<SeettuGroup[]> {
    return this.http.get<SeettuGroup[]>(`${this.apiUrl}/my-groups`, {
      headers: this.getHeaders()
    });
  }

  // Notification methods
  getNotifications(): Observable<Notification[]> {
    console.log('Making request to:', `${this.apiUrl}/notifications`);
    console.log('Headers:', this.getHeaders());
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications`, {
      headers: this.getHeaders()
    });
  }

  getUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/notifications/unread`, {
      headers: this.getHeaders()
    });
  }

  markNotificationAsRead(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/notifications/${id}/mark-read`, {}, {
      headers: this.getHeaders()
    });
  }

  getUnreadNotificationCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/notifications/unread-count`, {
      headers: this.getHeaders()
    });
  }

  // New methods for subscriber payment details
  getGroupPayments(groupId: number): Observable<PaymentStatus[]> {
    return this.http.get<PaymentStatus[]>(`${this.apiUrl}/groups/${groupId}/my-payments`, {
      headers: this.getHeaders()
    });
  }

  getGroupDetailsForSubscriber(groupId: number): Observable<SubscriberGroupDetails> {
    return this.http.get<SubscriberGroupDetails>(`${this.apiUrl}/subscriber/group/${groupId}/details`, {
      headers: this.getHeaders()
    });
  }

  // New method for cancelling groups
  cancelGroup(groupId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/groups/${groupId}/cancel`, {}, {
      headers: this.getHeaders()
    });
  }
}