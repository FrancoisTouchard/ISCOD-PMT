import { Contributor } from '../models/contributor.model';
import { Priority } from '../models/priority.enum';
import { TaskStatus } from '../models/taskStatus.enum';
import { formatDate } from './date.utils';
import { getPriorityLabel, getStatusLabel } from './labels.utils';

export function formatHistoryFieldValue(
  fieldName: string,
  value: string,
  users?: Contributor[]
): string {
  if (!value || value.trim() === '') {
    return '(vide)';
  }

  switch (fieldName) {
    case 'priority':
      return getPriorityLabel(value as Priority).label;

    case 'status':
      return getStatusLabel(value as TaskStatus).label;

    case 'dueDate':
    case 'endDate':
      return formatDate(value);

    case 'assigneeIds':
      return formatAssigneeValue(value, users);

    default:
      return value;
  }
}

function formatAssigneeValue(
  value: string,
  contributors?: Contributor[]
): string {
  if (value === '(aucun)') {
    return '(aucun)';
  }

  if (!contributors || contributors.length === 0) {
    return value;
  }

  const ids = value.split(', ').map((id) => id.trim());

  const userNames = ids
    .map((id) => {
      const user = contributors.find((c) => c.id.idUser === id);
      return user ? `${user.userName} (${user.userEmail})` : id;
    })
    .filter(Boolean);

  return userNames.length > 0 ? userNames.join(', ') : '(aucun)';
}
