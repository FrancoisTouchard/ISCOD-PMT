import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryEntry } from '../../../models/historyEntry.model';
import { getTaskFieldLabel } from '../../../utils/labels';
import { formatDate } from '../../../utils/dateFormatter';

@Component({
  selector: 'app-history-modal',
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './history-modal.component.html',
  styleUrl: './history-modal.component.scss',
})
export class HistoryModalComponent {
  @Input() isOpen = false;
  @Input() taskName?: string;
  @Input() historyEntries: HistoryEntry[] = [];
  @Input() loading = false;

  @Output() closed = new EventEmitter<void>();

  formatDate = formatDate;
  getTaskFieldLabel = getTaskFieldLabel;

  close(): void {
    this.closed.emit();
  }
}
