
import { Injectable } from "@angular/core"; 
import { HttpClient } from "@angular/common/http";
import { Observable, tap } from "rxjs";

interface AuthResponse {
    token: string;
    role: string;
    email: string;
}

interface RegisterRequest {
    name: string;
    email: string;
    password: string;
    role: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private baseUrl = 'http://localhost:8080/api/auth';
    
    constructor(private http: HttpClient) {}

    login(email: string, password: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { email, password })
            .pipe(
                tap(response => {
                    localStorage.setItem('token', response.token);
                    localStorage.setItem('userRole', response.role);
                    localStorage.setItem('userEmail', response.email);
                })
            );
    }

    register(registerData: RegisterRequest): Observable<any> {
        return this.http.post(`${this.baseUrl}/register`, registerData);
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }

    getToken(): string | null {
        return localStorage.getItem('token');
    }

    getUserRole(): string | null {
        return localStorage.getItem('userRole');
    }

    getUserEmail(): string | null {
        return localStorage.getItem('userEmail');
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userEmail');
    }
}