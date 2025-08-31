import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-contact',
  imports: [CommonModule, MatIconModule, MatCardModule],
  templateUrl: './contact.html',
  styleUrl: './contact.scss'
})
export class Contact {

}
