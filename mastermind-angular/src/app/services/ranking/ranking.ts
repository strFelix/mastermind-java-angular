import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

export interface RankingEntry {
  username: string;
  bestScore: number;
}

@Injectable({ providedIn: 'root' })
export class RankingService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  getRanking() {
    return this.http.get<RankingEntry[]>(`${this.baseUrl}/ranking`);
  }
}
