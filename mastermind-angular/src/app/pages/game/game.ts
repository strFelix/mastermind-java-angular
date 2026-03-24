import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GameService, GuessResponse } from '../../services/game/game';
import { HeaderComponent } from "../../components/header/header";
import { GameBoardRowComponent } from "../../components/game-board-row/game-board-row";
import { GameResultComponent } from "../../components/game-result/game-result";

interface AttemptRow {
  guess: string;
  correctPositions: number;
}

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, GameBoardRowComponent, GameResultComponent],
  templateUrl: './game.html',
})
export class Game implements OnInit {
  private game = inject(GameService);
  private router      = inject(Router);

  gameId       = signal<number | null>(null);
  currentGuess = signal('');
  attempts     = signal<AttemptRow[]>([]);
  currentTurn  = signal(1);
  loading      = signal(false);
  finished     = signal(false);
  won          = signal(false);
  secretCode = signal<string | null>(null);
  score        = signal(0);
  showModal    = signal(false);

  readonly maxAttempts = 10;
  readonly letters     = ['A', 'B', 'C', 'D'];

  get emptyRows() {
    return Array(Math.max(0, this.maxAttempts - this.attempts().length - (this.finished() ? 0 : 1))).fill(null);
  }

  ngOnInit() {
    const state = history.state;
    if (state?.gameId) {
      this.gameId.set(state.gameId);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  onGuessInput(value: string) {
    this.currentGuess.set(value.toUpperCase().replace(/[^A-D]/g, '').slice(0, 4));
  }

  submitGuess() {
    const guess = this.currentGuess();
    if (guess.length !== 4 || this.loading() || this.finished()) return;

    this.loading.set(true);

    this.game.makeGuess(this.gameId()!, guess).subscribe({
      next: (res: GuessResponse) => {
        this.attempts.update(prev => [...prev, { guess, correctPositions: res.correctPositions }]);
        this.currentTurn.update(t => t + 1);
        this.currentGuess.set('');
        this.loading.set(false);

        if (res.finished) {
          this.finished.set(true);
          this.won.set(res.won);

          if (res.won) {
            this.game.getGame(this.gameId()!).subscribe({
              next: (game) => this.score.set(game.score ?? 0)
            });
          } else {
            this.secretCode.set(res.secretCode);
          }
        }
      },
      error: () => this.loading.set(false)
    });
  }

  getCells(guess: string): string[] {
    return Array.from({ length: 4 }, (_, i) => guess[i] ?? '');
  }

  confirmQuit() { this.showModal.set(true); }
  cancelQuit()  { this.showModal.set(false); }
  quit()        { this.router.navigate(['/dashboard']); }
}
