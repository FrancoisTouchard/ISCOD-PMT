import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectComponent } from './project.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { TaskService } from '../../services/task.service';
import { ContributorService } from '../../services/contributor.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { ErrorHandlerService } from '../../services/errorHandler.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Role } from '../../models/role.enum';
import { TaskStatus } from '../../models/taskStatus.enum';
import { Priority } from '../../models/priority.enum';
import { provideHttpClient } from '@angular/common/http';

describe('ProjectComponent', () => {
  let component: ProjectComponent;
  let fixture: ComponentFixture<ProjectComponent>;

  let projectServiceSpy: jasmine.SpyObj<ProjectService>;
  let taskServiceSpy: jasmine.SpyObj<TaskService>;
  let contributorServiceSpy: jasmine.SpyObj<ContributorService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let toastSpy: jasmine.SpyObj<ToastService>;
  let errorSpy: jasmine.SpyObj<ErrorHandlerService>;

  const mockProject = {
    id: '1',
    name: 'Test Project',
    description: 'Desc',
    contributors: [],
  } as any;

  beforeEach(async () => {
    projectServiceSpy = jasmine.createSpyObj('ProjectService', [
      'getProjectById',
    ]);

    taskServiceSpy = jasmine.createSpyObj(
      'TaskService',
      ['getTasksByProjectId', 'createTask', 'updateTask', 'deleteTaskById'],
      { tasks: [] }
    );

    contributorServiceSpy = jasmine.createSpyObj('ContributorService', [
      'addContributor',
      'deleteContributor',
      'updateContributorRole',
    ]);

    projectServiceSpy.getProjectById.and.returnValue(of(mockProject));
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);
    toastSpy = jasmine.createSpyObj('ToastService', ['showToast']);
    errorSpy = jasmine.createSpyObj('ErrorHandlerService', ['handleError']);

    await TestBed.configureTestingModule({
      imports: [ProjectComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ProjectService, useValue: projectServiceSpy },
        { provide: TaskService, useValue: taskServiceSpy },
        { provide: ContributorService, useValue: contributorServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: ErrorHandlerService, useValue: errorSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: () => '1' } } },
        },
        {
          provide: Router,
          useValue: {
            navigate: jasmine.createSpy('navigate'),
            getCurrentNavigation: () => ({
              extras: { state: { project: mockProject } },
            }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectComponent);
    component = fixture.componentInstance;

    taskServiceSpy.getTasksByProjectId.and.returnValue(of([]));

    fixture.detectChanges();
  });

  // BASIC CREATION
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // INIT WITH PROJECT FROM NAVIGATION STATE
  it('should load project from navigation state', () => {
    expect(component.project?.id).toBe('1');
    expect(taskServiceSpy.getTasksByProjectId).toHaveBeenCalledWith('1');
  });

  // NO STATE => FALLBACK LOAD PROJECT VIA API
  it('should fallback to API load if no project in navigation state', () => {
    const router = TestBed.inject(Router);
    (router.getCurrentNavigation as any) = () => null;

    projectServiceSpy.getProjectById.and.returnValue(of(mockProject));

    component.ngOnInit();

    expect(projectServiceSpy.getProjectById).toHaveBeenCalledWith('1');
  });

  // BEHAVIOUR METHODS
  it('should set active tab', () => {
    component.setActiveTab('members');
    expect(component.activeTab).toBe('members');
  });

  it('should show add contributor modal', () => {
    component.showAddContributorBlock();
    expect(component.isMemberModalOpen).toBeTrue();
  });

  it('should close contributor modal and reset form', () => {
    component.isMemberModalOpen = true;
    component.addContributorForm.patchValue({
      contributorData: { email: 'test@mail.com', role: 'MEMBRE' },
    });

    component.closeMemberModal();

    expect(component.isMemberModalOpen).toBeFalse();
    expect(component.addContributorForm.value.contributorData.email).toBeNull();
  });

  // TASK HANDLERS
  it('should call taskService.createTask on add', () => {
    taskServiceSpy.createTask.and.returnValue(
      of({
        id: '28',
        name: 'Tasktest',
        dueDate: '25/12/2026',
        status: TaskStatus.TODO,
        priority: Priority.MEDIUM,
      })
    );

    component.onTaskAdded({ name: 'A' } as any);

    expect(taskServiceSpy.createTask).toHaveBeenCalled();
    expect(toastSpy.showToast).toHaveBeenCalled();
  });

  it('should call taskService.updateTask on update', () => {
    taskServiceSpy.updateTask.and.returnValue(
      of({
        id: '2',
        name: 'Updated',
        dueDate: '25/11/2028',
        status: TaskStatus.DONE,
        priority: Priority.CRITICAL,
      })
    );

    component.onTaskUpdated({
      taskId: '10',
      updatedTask: { name: 'X' } as any,
    });

    expect(taskServiceSpy.updateTask).toHaveBeenCalled();
    expect(toastSpy.showToast).toHaveBeenCalled();
  });

  // it('should call taskService.deleteTaskById on delete', () => {
  //   taskServiceSpy.deleteTaskById.and.returnValue(of(void 0));

  //   taskServiceSpy.getTasksByProjectId.and.returnValue(of([])); // <--- ajout

  //   component.onTaskDeleted('10');

  //   expect(taskServiceSpy.deleteTaskByProjectId).toHaveBeenCalledWith('10');
  // });

  // CONTRIBUTOR HANDLERS
  it('should add contributor if form valid', () => {
    contributorServiceSpy.addContributor.and.returnValue(
      of({
        id: { idUser: 'lalala', idProject: 'yo' },
        userEmail: 'test@mail.com',
        userName: 'test',
        role: Role.MEMBRE,
      })
    );
    projectServiceSpy.getProjectById.and.returnValue(of(mockProject));
    taskServiceSpy.getTasksByProjectId.and.returnValue(of([]));

    component.isMemberModalOpen = true;
    component.addContributorForm.patchValue({
      contributorData: { email: 'test@mail.com', role: 'MEMBRE' },
    });

    component.onContributorAdded();

    expect(contributorServiceSpy.addContributor).toHaveBeenCalled();
    expect(toastSpy.showToast).toHaveBeenCalled();
  });

  it('should delete contributor', () => {
    contributorServiceSpy.deleteContributor.and.returnValue(of(void 0));

    component.onContributorDeleted('100');

    expect(contributorServiceSpy.deleteContributor).toHaveBeenCalled();
  });

  it('should update contributor role', () => {
    contributorServiceSpy.updateContributorRole.and.returnValue(
      of({
        id: { idUser: 'ouaip', idProject: 'hello' },
        userName: 'Joe',
        userEmail: 'joe@mail.com',
        role: Role.OBSERVATEUR,
      })
    );

    component.onContributorRoleUpdated({
      contributorId: '1',
      newRole: Role.MEMBRE,
    });

    expect(contributorServiceSpy.updateContributorRole).toHaveBeenCalled();
  });
});
