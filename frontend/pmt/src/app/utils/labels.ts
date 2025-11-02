import { Priority } from '../models/priority.enum';
import { Role } from '../models/role.enum';
import { TaskStatus } from '../models/taskStatus.enum';

export function getRoleLabel(role: Role): { label: string; color: string } {
  switch (role) {
    case Role.ADMINISTRATEUR:
      return { label: 'Administrateur', color: '#6610f2' };
    case Role.MEMBRE:
      return { label: 'Membre', color: '#20c997' };
    case Role.OBSERVATEUR:
      return { label: 'Observateur', color: '#fd7e14' };
    default:
      return { label: role, color: '#6c757d' };
  }
}

export function getPriorityLabel(priority: Priority): {
  label: string;
  color: string;
} {
  switch (priority) {
    case Priority.LOW:
      return { label: 'Faible', color: '#110dfdff' };
    case Priority.MEDIUM:
      return { label: 'Moyenne', color: '#20c997' };
    case Priority.HIGH:
      return { label: 'Haute', color: '#6f42c1' };
    case Priority.CRITICAL:
      return { label: 'Critique', color: '#dc3545' };
    default:
      return { label: priority, color: '#6c757d' };
  }
}

export function getStatusLabel(status: TaskStatus): {
  label: string;
  color: string;
} {
  switch (status) {
    case TaskStatus.TODO:
      return { label: 'À faire', color: '#f9d90aff' };
    case TaskStatus.IN_PROGRESS:
      return { label: 'En cours', color: '#19cfcfff' };
    case TaskStatus.DONE:
      return { label: 'Fait', color: '#af33bcff' };
    case TaskStatus.BLOCKED:
      return { label: 'Bloqué', color: '#e74c5c' };
    default:
      return { label: status, color: '#6c757d' };
  }
}

export function getTaskFieldLabel(fieldName: string): string {
  const labels: { [key: string]: string } = {
    name: 'Nom',
    description: 'Description',
    dueDate: "Date d'échéance",
    endDate: 'Date de fin',
    priority: 'Priorité',
    status: 'Statut',
    assigneeIds: 'Assignation',
  };
  return labels[fieldName] || fieldName;
}
