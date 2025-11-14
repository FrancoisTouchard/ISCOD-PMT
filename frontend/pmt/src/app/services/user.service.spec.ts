import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';
import { ApiService } from './api.service';
import { of } from 'rxjs';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('UserService', () => {
  let service: UserService;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj('ApiService', ['postUser', 'getUsers']);
    apiSpy.getUsers.and.returnValue(of([]));

    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClientTesting(),
        { provide: ApiService, useValue: apiSpy },
      ],
    });

    service = TestBed.inject(UserService);
  });

  it('should add a user', (done) => {
    const mockUser = {
      id: '1',
      name: 'Test',
      email: 'a@b.com',
      password: '123',
    };
    apiSpy.postUser.and.returnValue(of(mockUser));

    service.addUser('Test', 'a@b.com', '123').subscribe((res) => {
      expect(res).toEqual(mockUser);
      expect(service.users).toContain(mockUser);
      done();
    });
  });
});
