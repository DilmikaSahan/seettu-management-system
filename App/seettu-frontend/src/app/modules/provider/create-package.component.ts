import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SeettuService, SeettuPackage } from '../../services/seettu.service';

@Component({
  selector: 'app-create-package',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './create-package.component.html',
  styleUrls: ['./create-package.component.scss']
})
export class CreatePackageComponent {
  packageData: Omit<SeettuPackage, 'id'> = {
    packageName: '',
    description: '',
    packageValue: 0
  };

  isSubmitting = false;

  constructor(
    private seettuService: SeettuService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.isSubmitting) return;

    this.isSubmitting = true;

    this.seettuService.createPackage(this.packageData).subscribe({
      next: (response) => {
        alert('Package created successfully!');
        this.router.navigate(['/provider/dashboard']);
      },
      error: (error) => {
        console.error('Error creating package:', error);
        alert('Error creating package. Please try again.');
        this.isSubmitting = false;
      }
    });
  }

  goBack() {
    this.router.navigate(['/provider/dashboard']);
  }
}