import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SeettuService, AddSubscriberRequest } from '../../services/seettu.service';

@Component({
  selector: 'app-add-subscriber',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-subscriber.component.html',
  styleUrls: ['./add-subscriber.component.scss']
})
export class AddSubscriberComponent {
  subscriberData: AddSubscriberRequest = {
    name: '',
    email: '',
    phoneNumber: '',
    password: ''
  };

  confirmPassword = '';
  isSubmitting = false;

  constructor(
    private seettuService: SeettuService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.isSubmitting) return;

    if (this.subscriberData.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.isSubmitting = true;

    this.seettuService.addSubscriber(this.subscriberData).subscribe({
      next: (response) => {
        alert('Subscriber added successfully!');
        this.router.navigate(['/provider/dashboard']);
      },
      error: (error) => {
        console.error('Error adding subscriber:', error);
        if (error.error?.message) {
          alert('Error: ' + error.error.message);
        } else {
          alert('Error adding subscriber. Please try again.');
        }
        this.isSubmitting = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/provider/dashboard']);
  }
}