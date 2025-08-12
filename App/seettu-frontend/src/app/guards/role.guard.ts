import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../modules/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRole = route.data['expectedRole'];
    const userRole = this.authService.getUserRole();
    
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (userRole !== expectedRole) {
      // Redirect to appropriate dashboard based on user role
      if (userRole === 'PROVIDER') {
        this.router.navigate(['/provider/dashboard']);
      } else if (userRole === 'SUBSCRIBER') {
        this.router.navigate(['/subscriber/dashboard']);
      } else {
        this.router.navigate(['/login']);
      }
      return false;
    }

    return true;
  }
}