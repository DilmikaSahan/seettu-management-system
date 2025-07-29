import { Injectable } from "@angular/core"; 
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

interface AuthResponse {
    token: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl =  'http://localhost:8080/api/auth/login'; 
    constructor(private http: HttpClient) {}

    login(username: string, password: string): Observable<AuthResponse>{
        return this.http.post<AuthResponse>(this.apiUrl, { username, password });
    }

    isLoggedIn(): boolean {
        return !!localStorage.getItem('token');
    }
    logout() {
        localStorage.removeItem('token');
    }
    }