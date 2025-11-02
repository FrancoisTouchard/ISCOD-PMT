import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subscription, tap } from 'rxjs';
import { ApiService } from './api.service';
import { LocalTask, Task } from '../models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TaskService implements OnDestroy {
  tasks: Task[] = [];
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {}

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  getTasksByProjectId(projectId: string) {
    return this.apiService.getTasksByProjectId(projectId).pipe(
      tap((tasks) => {
        this.tasks = tasks;
      })
    );
  }

  createTask(projectId: string, task: LocalTask) {
    return this.apiService.postTask(projectId, task).pipe(
      tap((newTask) => {
        this.tasks = [...this.tasks, newTask];
      })
    );
  }

  updateTask(
    projectId: string,
    userId: string,
    taskId: string,
    updatedTask: LocalTask
  ): Observable<Task> {
    return this.apiService.patchTask(projectId, userId, taskId, updatedTask);
  }

  deleteTaskById(taskId: string) {
    return this.apiService.deleteTaskById(taskId).pipe(
      tap((tasks) => {
        this.tasks = tasks;
      })
    );
  }
}
