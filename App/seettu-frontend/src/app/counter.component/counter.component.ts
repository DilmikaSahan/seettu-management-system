import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-counter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './counter.component.html',
  styleUrls: ['./counter.component.scss']
})
export class CounterComponent implements AfterViewInit {
  @ViewChild('counterElement') counterElement!: ElementRef;

  count: number = 0;
  hasAnimated: boolean = false;

  ngAfterViewInit() {
    const observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && !this.hasAnimated) {
        this.animateCounter();
        this.hasAnimated = true;
      }
    });

    observer.observe(this.counterElement.nativeElement);
  }

  animateCounter() {
    const target = 100;
    const duration = 2000; 
    const interval = 10;
    let current = 0;
    const step = target / (duration / interval);

    const counter = setInterval(() => {
      current += step;
      this.count = Math.min(Math.round(current), target);
      if (this.count >= target) clearInterval(counter);
    }, interval);
  }
}
