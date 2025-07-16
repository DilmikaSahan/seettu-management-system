import { Component } from '@angular/core';
import { MatToolbar } from "@angular/material/toolbar";
import { HeroSlider } from "../../hero-slider/hero-slider";

@Component({
  selector: 'app-home',
  imports: [MatToolbar, HeroSlider],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {

}
