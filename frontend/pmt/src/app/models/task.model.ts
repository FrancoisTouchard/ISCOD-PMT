import { Priority } from './priority.enum';
import { TaskAssignment } from './taskAssignment.model';
import { TaskStatus } from './taskStatus.enum';

export interface LocalTask {
  name: string;
  description?: string;
  dueDate: string;
  endDate?: string;
  priority: Priority;
  status?: TaskStatus;
  assignments?: TaskAssignment[];
  assigneeIds?: String[];
}

export interface Task extends LocalTask {
  id: string;
}
