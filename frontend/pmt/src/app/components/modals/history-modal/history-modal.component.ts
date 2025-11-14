import { CommonModule, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HistoryEntry } from '../../../models/historyEntry.model';
import { getTaskFieldLabel } from '../../../utils/labels.utils';
import { formatDateWithMinutes } from '../../../utils/date.utils';
import { formatHistoryFieldValue } from '../../../utils/history.utils';
import { Contributor } from '../../../models/contributor.model';

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
  @Input() contributors: Contributor[] = [];

  @Output() closed = new EventEmitter<void>();

  formatDateWithMinutes = formatDateWithMinutes;
  getTaskFieldLabel = getTaskFieldLabel;

  close(): void {
    this.closed.emit();
  }

  formatFieldValue(fieldName: string, value: string): string {
    return formatHistoryFieldValue(fieldName, value, this.contributors);
  }
}
