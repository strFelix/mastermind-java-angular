import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game-board-row',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-board-row.html',
})
export class GameBoardRowComponent {
  @Input() rowNumber = 0;
  @Input() guess = '';
  @Input() correctPositions = 0;
  @Input() isActive = false;
  @Input() isEmpty = false;

  getCells(): string[] {
    return Array.from({ length: 4 }, (_, i) => this.guess[i] ?? '');
  }
}
