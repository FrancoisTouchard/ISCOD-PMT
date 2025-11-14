import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SigninComponent } from './signin.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('SigninComponent', () => {
  let component: SigninComponent;
  let fixture: ComponentFixture<SigninComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UserService', ['addUser']);
    const routeSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [SigninComponent],
      providers: [
        provideHttpClientTesting(),
        { provide: UserService, useValue: userSpy },
        { provide: Router, useValue: routeSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SigninComponent);
    component = fixture.componentInstance;
    userServiceSpy = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not submit with invalid form', () => {
    component.loginForm.setValue({
      userData: { name: '', email: 'invalid', password: '123' },
    });
    component.onSubmit();
    expect(component.submitted).toBeTrue();
    expect(userServiceSpy.addUser).not.toHaveBeenCalled();
  });

  it('should submit valid form and navigate to login', (done) => {
    userServiceSpy.addUser.and.returnValue(
      of({
        id: '10',
        name: 'Joe',
        email: 'joe@mail.com',
        password: '123456',
      })
    );

    component.loginForm.setValue({
      userData: { name: 'Joe', email: 'joe@mail.com', password: '123456' },
    });

    component.onSubmit();

    setTimeout(() => {
      expect(userServiceSpy.addUser).toHaveBeenCalledWith(
        'Joe',
        'joe@mail.com',
        '123456'
      );
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
      done();
    }, 0);
  });

  it('should navigate to login page', () => {
    component.goToLoginPage();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
