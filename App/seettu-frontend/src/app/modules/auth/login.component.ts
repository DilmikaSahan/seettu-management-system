import { Component } from '@angular/core';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { F } from '@angular/cdk/keycodes';
import { NgIf } from '@angular/common'; 

@Component({
  selector: 'app-login.component',
  imports: [FormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  username : string = '';
  password : string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private roter: Router){}
  login(){
    this.authService.login(this.username, this.password).subscribe({
      next: (res) =>{
        localStorage.setItem('token',res.token);
        this.roter.navigate(['/']);
      },
      error: (err) =>{
        this.errorMessage ='Invalid username or password';
        console.error('Login error:', err);
      }
    })
  }

}
