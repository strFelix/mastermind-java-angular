import { Component, inject, signal, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { Auth } from '../../services/auth/auth';
import { Game, GameResponse } from '../../services/game/game';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private auth   = inject(Auth);
  private game   = inject(Game);
  private router = inject(Router);

  username  = this.auth.getUsername();
  bestScore = this.auth.getBestScore();

  history = signal<GameResponse[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.game.getHistory().subscribe({
      next: (data) => this.history.set(data),
      error: () => {}
    });
  }

  startGame() {
    this.loading.set(true);
    this.game.startGame().subscribe({
      next: (game) => this.router.navigate(['/game'], { state: { gameId: game.id } }),
      error: () => this.loading.set(false)
    });
  }

  logout() {
    this.auth.logout();
  }
}
