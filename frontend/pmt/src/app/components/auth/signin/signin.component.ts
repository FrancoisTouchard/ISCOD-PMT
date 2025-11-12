import { NgClass, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, FormsModule, NgClass],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.scss',
})
export class SigninComponent {
  loginForm: FormGroup;
  submitted = false;

  constructor(private userService: UserService, private router: Router) {
    this.loginForm = new FormGroup({
      userData: new FormGroup({
        name: new FormControl(null, [Validators.required]),
        email: new FormControl(null, [Validators.required, Validators.email]),
        password: new FormControl(null, [
          Validators.required,
          Validators.minLength(6),
        ]),
      }),
    });
  }

  onSubmit() {
    this.submitted = true;
    if (this.loginForm.invalid) return;
    const name = this.name.value;
    const email = this.email.value;
    const password = this.password.value;
    this.userService.addUser(name, email, password).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
    });
  }

  get name() {
    return this.loginForm.get('userData.name') as FormControl;
  }

  get email() {
    return this.loginForm.get('userData.email') as FormControl;
  }

  get password() {
    return this.loginForm.get('userData.password') as FormControl;
  }

  goToLoginPage() {
    this.router.navigate(['/login']);
  }
}
