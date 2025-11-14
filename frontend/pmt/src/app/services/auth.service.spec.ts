import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

describe('AuthService', () => {
  let service: AuthService;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      providers: [AuthService, { provide: Router, useValue: routerSpy }],
    });

    service = TestBed.inject(AuthService);
    localStorage.clear();
  });

  it('should login and logout', () => {
    service.login('user-1');
    expect(service.isLoggedIn).toBeTrue();
    expect(service.userId).toBe('user-1');

    service.logout();
    expect(service.isLoggedIn).toBeFalse();
    expect(service.userId).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
