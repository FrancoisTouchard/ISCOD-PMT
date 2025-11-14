import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUsers']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    // On définit la valeur de retour AVANT la création du composant
    userServiceSpy.getUsers.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClientTesting(),
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fail login with invalid credentials', fakeAsync(() => {
    userServiceSpy.getUsers.and.returnValue(
      of([{ id: '1', email: 'a@b.com', name: 'coucou', password: '123456' }])
    );

    component.loginForm.setValue({
      userData: { email: 'wrong@b.com', password: 'wrongpass' },
    });

    component.onSubmit();
    tick();

    expect(component.loginForm.errors?.['invalidCredentials']).toBeTrue();
  }));

  it('should login with valid credentials', fakeAsync(() => {
    const mockUser = {
      id: '1',
      email: 'a@b.com',
      name: 'coucou',
      password: '123456',
    };
    userServiceSpy.getUsers.and.returnValue(of([mockUser]));

    component.loginForm.setValue({
      userData: { email: 'a@b.com', password: '123456' },
    });

    component.onSubmit();
    tick();

    expect(authServiceSpy.login).toHaveBeenCalledWith('1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['home']);
  }));

  it('should navigate to signin page', () => {
    component.goToSigninPage();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/signin']);
  });
});
