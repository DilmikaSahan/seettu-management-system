import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-counter',
  imports: [CommonModule],
  templateUrl: './counter.html',
  styleUrl: './counter.scss'
})
export class Counter implements AfterViewInit {
  @ViewChild('counterElement') counterElement!: ElementRef;
  count = 0;
  target = 100;

  ngAfterViewInit() {
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) {
        this.animateCounter();
        observer.disconnect();
      }
    });
    observer.observe(this.counterElement.nativeElement);
  }

  animateCounter() {
    const increment = this.target / 100;
    const timer = setInterval(() => {
      this.count += increment;
      if (this.count >= this.target) {
        this.count = this.target;
        clearInterval(timer);
      }
    }, 20);
  }
}
