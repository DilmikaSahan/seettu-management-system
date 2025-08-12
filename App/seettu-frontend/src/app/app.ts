import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NavigationComponent } from './components/navigation.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatToolbarModule, FormsModule, NavigationComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'seettu-frontend';
}
