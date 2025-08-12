import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hero-slider',
  imports: [CommonModule],
  templateUrl: './hero-slider.html',
  styleUrl: './hero-slider.scss'
})
export class HeroSlider implements OnInit,OnDestroy {
  images =[
    "hero01a.jpg",
    "hero02.jpg",
    "hero03.jpg",
    "hero04.jpg"

  ];
  currentIndex=0;
  intervalID?:any;

  ngOnInit(): void {
    this.startSlideshow();
  }
  ngOnDestroy(): void {
    clearInterval(this.intervalID)
  }
  startSlideshow(){
    this.intervalID=setInterval(()=>{
    this.currentIndex=(this.currentIndex+1)%this.images.length;
    },3000);
  }


}
