import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ProjectService } from './project.service';
import { ApiService } from './api.service';
import { Project, LocalProject } from '../models/project.model';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ProjectService', () => {
  let service: ProjectService;
  let apiServiceSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiServiceSpy = jasmine.createSpyObj('ApiService', [
      'getProjectsByContributeur',
      'getProjectById',
      'postProject',
      'deleteProject',
    ]);

    TestBed.configureTestingModule({
      providers: [
        ProjectService,
        provideHttpClientTesting(),
        { provide: ApiService, useValue: apiServiceSpy },
      ],
    });

    service = TestBed.inject(ProjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get projects by contributor', (done) => {
    const projects: Project[] = [
      {
        id: '1',
        name: 'Test',
        description: '',
        startDate: '',
        contributors: [],
      },
    ];
    apiServiceSpy.getProjectsByContributeur.and.returnValue(of(projects));

    service.getProjectsByContributeur('user-1').subscribe((res) => {
      expect(res).toEqual(projects);
      expect(service.projects).toEqual(projects);
      done();
    });
  });

  it('should create a project', (done) => {
    const newProject: Project = {
      id: '1',
      name: 'New',
      description: '',
      startDate: '',
      contributors: [],
    };
    apiServiceSpy.postProject.and.returnValue(of(newProject));

    service.createProject('New', '', '', 'user-1').subscribe((res) => {
      expect(res).toEqual(newProject);
      done();
    });
  });
});
