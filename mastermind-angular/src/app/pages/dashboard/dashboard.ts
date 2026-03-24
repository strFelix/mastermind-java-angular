import { Component, inject, signal, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../services/auth/auth';
import { GameService, GameResponse } from '../../services/game/game';
import { HeaderComponent } from "../../components/header/header";
import { HistoryRowComponent } from "../../components/history-row/history-row";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, HeaderComponent, HistoryRowComponent],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private auth   = inject(AuthService);
  private game   = inject(GameService);
  private router = inject(Router);

  username  = this.auth.getUsername();
  bestScore = signal(0);

  history = signal<GameResponse[]>([]);
  loading = signal(false);

  ngOnInit() {
    this.auth.getMe().subscribe({
      next: (user) => this.bestScore.set(user.bestScore)
    });

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
