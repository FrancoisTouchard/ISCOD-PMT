import { TestBed } from '@angular/core/testing';
import { HistoryService } from './history.service';
import { ApiService } from './api.service';
import { HistoryEntry } from '../models/historyEntry.model';
import { of } from 'rxjs';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('HistoryService', () => {
  let service: HistoryService;
  let apiSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj('ApiService', [
      'getTasksModificationsByProjectId',
      'getTasksModificationsByTaskId',
    ]);
    TestBed.configureTestingModule({
      providers: [
        HistoryService,
        provideHttpClientTesting(),
        { provide: ApiService, useValue: apiSpy },
      ],
    });
    service = TestBed.inject(HistoryService);
  });

  it('should get history entries by project id', (done) => {
    const entries: HistoryEntry[] = [
      {
        id: '1',
        projectId: 'p1',
        taskId: 't1',
        userId: 'u1',
        userName: 'Tutu',
        taskName: 'Task 1',
        modifiedFieldName: 'status',
        oldFieldValue: 'todo',
        newFieldValue: 'done',
        modifiedAt: new Date().toISOString(),
      },
    ];

    apiSpy.getTasksModificationsByProjectId.and.returnValue(of(entries));

    service.getHistoryEntriesByProjectId('p1').subscribe((res) => {
      expect(res).toEqual(entries);
      expect(service.historyEntries).toEqual(entries);
      done();
    });
  });

  it('should get history entries by task id', (done) => {
    const entries: HistoryEntry[] = [
      {
        id: '2',
        projectId: 'p1',
        taskId: 't2',
        userId: 'u2',
        userName: 'Jack',
        taskName: 'Task 2',
        modifiedFieldName: 'description',
        oldFieldValue: 'Old desc',
        newFieldValue: 'New desc',
        modifiedAt: new Date().toISOString(),
      },
    ];

    apiSpy.getTasksModificationsByTaskId.and.returnValue(of(entries));

    service.getHistoryEntriesByTaskId('t2').subscribe((res) => {
      expect(res).toEqual(entries);
      expect(service.historyEntries).toEqual(entries);
      done();
    });
  });
});
