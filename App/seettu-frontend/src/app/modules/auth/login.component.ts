import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common'; 
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login.component',
  imports: [FormsModule, NgIf, RouterLink, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.authService.login(this.email, this.password).subscribe({
      next: (res) => {
        // Redirect based on user role
        if (res.role === 'PROVIDER') {
          this.router.navigate(['/provider/dashboard']);
        } else if (res.role === 'SUBSCRIBER') {
          this.router.navigate(['/subscriber/dashboard']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.errorMessage = 'Invalid email or password';
        console.error('Login error:', err);
      }
    });
  }
}
