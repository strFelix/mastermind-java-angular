import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RankingService, RankingEntry } from '../../services/ranking/ranking';
import { HeaderComponent } from "../../components/header/header";

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, HeaderComponent],
  templateUrl: './ranking.html',
})
export class Ranking implements OnInit {
  private rankingService = inject(RankingService);
  private router         = inject(Router);

  ranking = signal<RankingEntry[]>([]);
  loading = signal(true);

  ngOnInit() {
    this.rankingService.getRanking().subscribe({
      next: (data) => {
        this.ranking.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }
}
