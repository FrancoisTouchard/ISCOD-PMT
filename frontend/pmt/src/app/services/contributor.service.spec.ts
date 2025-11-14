import { TestBed } from '@angular/core/testing';
import { ContributorService } from './contributor.service';
import { ApiService } from './api.service';
import { Role } from '../models/role.enum';
import { of } from 'rxjs';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ContributorService', () => {
  let service: ContributorService;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj('ApiService', [
      'postContributor',
      'deleteContributor',
      'patchContributorRole',
    ]);
    TestBed.configureTestingModule({
      providers: [
        ContributorService,
        provideHttpClientTesting(),
        { provide: ApiService, useValue: apiSpy },
      ],
    });
    service = TestBed.inject(ContributorService);
  });

  it('should add a contributor', (done) => {
    const mock = {
      id: { idUser: 'u1', idProject: 'p1' },
      role: Role.ADMINISTRATEUR,
      userName: 'Joe',
      userEmail: 'joe@mail.com',
    };
    apiSpy.postContributor.and.returnValue(of(mock));

    service
      .addContributor('p1', 'joe@mail.com', Role.ADMINISTRATEUR)
      .subscribe((res) => {
        expect(res).toEqual(mock);
        done();
      });
  });

  it('should delete a contributor', (done) => {
    apiSpy.deleteContributor.and.returnValue(of(void 0));

    service.deleteContributor('p1', 'u1').subscribe((res) => {
      expect(res).toBeUndefined();
      done();
    });
  });

  it('should update a contributor role', (done) => {
    const mock = {
      id: { idUser: 'u1', idProject: 'p1' },
      role: Role.MEMBRE,
      userName: 'Joe',
      userEmail: 'joe@mail.com',
    };
    apiSpy.patchContributorRole.and.returnValue(of(mock));

    service.updateContributorRole('p1', 'u1', Role.MEMBRE).subscribe((res) => {
      expect(res).toEqual(mock);
      done();
    });
  });
});
