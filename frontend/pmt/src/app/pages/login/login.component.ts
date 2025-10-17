import { NgClass, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = new FormGroup({
      userData: new FormGroup({
        email: new FormControl(null, [Validators.required, Validators.email]),
        password: new FormControl(null, [Validators.required]),
      }),
    });

    this.userService.getUsers().subscribe((usersData) => {
      const usersArray = Object.values(usersData);
      console.log('usersArray in login constructor', usersArray);
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) return;
    const email = this.email.value;
    const password = this.password.value;
    this.userService.getUsers().subscribe((usersData) => {
      const usersArray = Object.values(usersData);
      console.log('usersData in login constructor', usersData);
      const matchingUser = usersArray.find(
        (user) => user.email === email && user.password === password
      );

      if (matchingUser) {
        this.authService.login();
        this.router.navigate(['home']);
      } else {
        this.loginForm.setErrors({ invalidCredentials: true });
      }
    });
  }

  get email() {
    return this.loginForm.get('userData.email') as FormControl;
  }

  get password() {
    return this.loginForm.get('userData.password') as FormControl;
  }

  goToSigninPage() {
    this.router.navigate(['/signin']);
  }
}
