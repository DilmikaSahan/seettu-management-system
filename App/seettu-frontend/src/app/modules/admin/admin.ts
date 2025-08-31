import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  role: string;
  createdDate: string;
  active: boolean;
}

export interface AdminDashboardStats {
  totalUsers: number;
  totalProviders: number;
  totalSubscribers: number;
  totalAdmins: number;
}

export interface CreateAdminRequest {
  name: string;
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  // Dashboard statistics
  getDashboardStats(): Observable<AdminDashboardStats> {
    return this.http.get<AdminDashboardStats>(`${this.baseUrl}/dashboard/stats`, {
      headers: this.getHeaders()
    });
  }

  // User management
  getAllUsers(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.baseUrl}/users`, {
      headers: this.getHeaders()
    });
  }

  searchUsers(term: string): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.baseUrl}/users/search?term=${encodeURIComponent(term)}`, {
      headers: this.getHeaders()
    });
  }

  getUsersByRole(role: string): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.baseUrl}/users/role/${role}`, {
      headers: this.getHeaders()
    });
  }

  getUserById(userId: number): Observable<UserSummary> {
    return this.http.get<UserSummary>(`${this.baseUrl}/users/${userId}`, {
      headers: this.getHeaders()
    });
  }

  // Admin creation
  createAdmin(request: CreateAdminRequest): Observable<UserSummary> {
    return this.http.post<UserSummary>(`${this.baseUrl}/users/admin`, request, {
      headers: this.getHeaders()
    });
  }

  // User role management
  updateUserRole(userId: number, role: string): Observable<UserSummary> {
    return this.http.put<UserSummary>(`${this.baseUrl}/users/${userId}/role`, { role }, {
      headers: this.getHeaders()
    });
  }

  // User deletion
  deleteUser(userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/${userId}`, {
      headers: this.getHeaders()
    });
  }
}
