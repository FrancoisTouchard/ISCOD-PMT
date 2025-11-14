import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { LandingComponent } from './landing.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('LandingComponent', () => {
  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LandingComponent],
      providers: [
        { provide: Router, useValue: mockRouter },
        provideHttpClientTesting,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to login page', () => {
    component.goToLoginPage();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to signin page', () => {
    component.goToSigninPage();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/signin']);
  });

  it('should call goToLoginPage when login button is clicked', () => {
    spyOn(component, 'goToLoginPage');
    component.goToLoginPage();
    expect(component.goToLoginPage).toHaveBeenCalled();
  });

  it('should call goToSigninPage when signin button is clicked', () => {
    spyOn(component, 'goToSigninPage');
    component.goToSigninPage();
    expect(component.goToSigninPage).toHaveBeenCalled();
  });
});
