import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-game-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-result.html',
})
export class GameResultComponent {
  @Input() won = false;
  @Input() attempts = 0;
  @Input() score = 0;
  @Input() secretCode: string | null = null;
  @Output() quit = new EventEmitter<void>();
}
