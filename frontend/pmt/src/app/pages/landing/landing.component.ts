import { Component } from '@angular/core';
import { Router } from '@angular/router';
// import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing',
  imports: [],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.scss',
})
export class LandingComponent {
  constructor(private router: Router /*, private authService: AuthService*/) {}

  goToLoginPage() {
    this.router.navigate(['/login']);
  }

  goToSigninPage() {
    this.router.navigate(['/signin']);
  }
}
