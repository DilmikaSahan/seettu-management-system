import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HeroSlider } from '../../hero-slider/hero-slider';
import { Counter } from '../../counter/counter';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    MatToolbarModule, 
    MatCardModule, 
    MatButtonModule, 
    MatIconModule,
    MatSnackBarModule,
    HeroSlider,
    Counter
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class Home implements OnInit {
  isLoggedIn = false;
  userRole: string | null = null;
  userEmail: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      this.userRole = this.authService.getUserRole();
      this.userEmail = this.authService.getUserEmail();
    }
  }

  getDashboardRoute(): string {
    if (this.userRole === 'PROVIDER') {
      return '/provider/dashboard';
    } else if (this.userRole === 'SUBSCRIBER') {
      return '/subscriber/dashboard';
    }
    return '/';
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  navigateToRegister(role?: string) {
    if (role) {
      this.router.navigate(['/register'], { queryParams: { role: role } });
    } else {
      this.router.navigate(['/register']);
    }
  }

  navigateToAbout() {
    this.router.navigate(['/about']);
  }

  navigateToContact() {
    this.router.navigate(['/contact']);
  }

  navigateToDashboard() {
    this.router.navigate([this.getDashboardRoute()]);
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
    this.userRole = null;
    this.userEmail = null;
    this.snackBar.open('Logged out successfully', 'Close', { duration: 3000 });
    this.router.navigate(['/']);
  }

  showProviderDetails() {
    // Navigate to About page instead of showing snackbar
    this.router.navigate(['/about']);
  }

  showSubscriberDetails() {
    // Navigate to About page instead of showing snackbar
    this.router.navigate(['/about']);
  }
}
