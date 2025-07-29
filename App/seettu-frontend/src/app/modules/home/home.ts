import { Component } from '@angular/core';
import { MatToolbar } from "@angular/material/toolbar";
import { HeroSlider } from "../../hero-slider/hero-slider";
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import { CounterComponent } from "../../counter.component/counter.component";
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';
@Component({
  selector: 'app-home',
  imports: [MatToolbar, HeroSlider, MatButtonModule, MatCardModule, CounterComponent,RouterLink,RouterModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {

}
