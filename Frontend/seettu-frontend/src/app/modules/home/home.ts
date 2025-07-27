import { Component } from '@angular/core';
import { MatToolbar } from "@angular/material/toolbar";
import { HeroSlider } from "../../hero-slider/hero-slider";
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';

@Component({
  selector: 'app-home',
  imports: [MatToolbar, HeroSlider, MatButtonModule, MatCardModule],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {

}
