import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MatToolbarModule} from '@angular/material/toolbar';
import { FormsModule  } from '@angular/forms';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,MatToolbarModule,FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'seettu-frontend';
}
