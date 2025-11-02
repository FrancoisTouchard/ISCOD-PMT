import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subscription, tap } from 'rxjs';
import { ApiService } from './api.service';
import { LocalTask, Task } from '../models/task.model';
import { HistoryEntry } from '../models/historyEntry.model';

@Injectable({
  providedIn: 'root',
})
export class HistoryService implements OnDestroy {
  historyEntries: HistoryEntry[] = [];
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {}

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  getHistoryEntriesByProjectId(projectId: string) {
    return this.apiService.getTasksModificationsByProjectId(projectId).pipe(
      tap((historyEntries) => {
        this.historyEntries = historyEntries;
      })
    );
  }

  getHistoryEntriesByTaskId(taskId: string) {
    return this.apiService.getTasksModificationsByTaskId(taskId).pipe(
      tap((historyEntries) => {
        this.historyEntries = historyEntries;
      })
    );
  }
}
