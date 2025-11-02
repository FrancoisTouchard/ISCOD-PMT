export interface HistoryEntry {
  id: string;
  projectId: string;
  taskId: string;
  userId: string;
  userName: string;
  taskName: string;
  modifiedFieldName: string;
  oldFieldValue: string | null;
  newFieldValue: string | null;
  modifiedAt: string;
}
