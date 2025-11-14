import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { HomeComponent } from './home.component';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { Project } from '../../models/project.model';
import { provideHttpClient } from '@angular/common/http';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let projectServiceMock: jasmine.SpyObj<ProjectService>;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let toastServiceMock: jasmine.SpyObj<ToastService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    projectServiceMock = jasmine.createSpyObj('ProjectService', [
      'getProjectsByContributeur',
      'createProject',
      'deleteProjectById',
      'projects',
    ]);

    authServiceMock = jasmine.createSpyObj('AuthService', ['logout']);
    toastServiceMock = jasmine.createSpyObj('ToastService', ['showToast']);
    routerMock = jasmine.createSpyObj('Router', ['navigate']);

    projectServiceMock.projects = [
      {
        id: '1',
        name: 'Projet Test',
        description: '',
        startDate: '',
        contributors: [],
      } as Project,
    ];
    projectServiceMock.getProjectsByContributeur.and.returnValue(
      of(projectServiceMock.projects)
    );

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ProjectService, useValue: projectServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;

    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      if (key === 'userId') return 'user-123';
      return null;
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load projects on init', () => {
    component.ngOnInit();
    expect(projectServiceMock.getProjectsByContributeur).toHaveBeenCalledWith(
      'user-123'
    );
    expect(component.projects.length).toBe(1);
  });

  it('should call logout when logOut is called', () => {
    component.logOut();
    expect(authServiceMock.logout).toHaveBeenCalled();
  });

  it('should open and close project creation modal', () => {
    component.openProjectCreationModal();
    expect(component.isModalOpen).toBeTrue();

    component.closeProjectCreationModal();
    expect(component.isModalOpen).toBeFalse();
  });

  it('should handle project deletion', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    projectServiceMock.deleteProjectById.and.returnValue(of([]));

    component.projects = [...projectServiceMock.projects];
    component.deleteProject('1');

    expect(projectServiceMock.deleteProjectById).toHaveBeenCalledWith('1');
    expect(toastServiceMock.showToast).toHaveBeenCalledWith(
      'Projet supprimé avec succès !',
      'success'
    );
    expect(component.projects.length).toBe(0);
  });

  it('should handle errors on project loading', () => {
    projectServiceMock.getProjectsByContributeur.and.returnValue(
      throwError(() => new Error('fail'))
    );
    component.loadProjects();
    expect(toastServiceMock.showToast).toHaveBeenCalledWith(
      'Erreur lors du chargement des données',
      'error'
    );
  });
});
