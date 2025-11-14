import { TestBed } from '@angular/core/testing';
import { TaskService } from './task.service';
import { ApiService } from './api.service';
import { LocalTask, Task } from '../models/task.model';
import { of } from 'rxjs';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Priority } from '../models/priority.enum';
import { TaskStatus } from '../models/taskStatus.enum';

describe('TaskService', () => {
  let service: TaskService;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj('ApiService', [
      'getTasksByProjectId',
      'postTask',
      'patchTask',
      'deleteTaskById',
    ]);
    TestBed.configureTestingModule({
      providers: [
        TaskService,
        provideHttpClientTesting(),
        { provide: ApiService, useValue: apiSpy },
      ],
    });
    service = TestBed.inject(TaskService);
  });

  it('should create a task', (done) => {
    const task: Task = {
      id: '1',
      name: 'T',
      description: '',
      dueDate: '',
      endDate: '',
      priority: Priority.HIGH,
      status: TaskStatus.IN_PROGRESS,
      assigneeIds: [],
    };
    apiSpy.postTask.and.returnValue(of(task));

    service.createTask('p1', {} as LocalTask).subscribe((res) => {
      expect(res).toEqual(task);
      expect(service.tasks).toContain(task);
      done();
    });
  });
});
