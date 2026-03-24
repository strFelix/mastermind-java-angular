import { Component, Input } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'app-history-row',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './history-row.html',
})
export class HistoryRowComponent {
  @Input() startTime = '';
  @Input() attempts = 0;
  @Input() score = 0;
  @Input() won = false;
}
