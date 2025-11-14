import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  imports: [],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  constructor(private router: Router) {}

  goToLoginPage() {
    this.router.navigate(['/login']);
  }

  goToSigninPage() {
    this.router.navigate(['/signin']);
  }
}
