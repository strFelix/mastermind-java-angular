import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface GameResponse {
  id: number;
  attempts: number;
  score: number;
  startTime: string;
  endTime: string;
  finished: boolean;
  won: boolean;
}

export interface GuessResponse {
  correctPositions: number;
  finished: boolean;
  won: boolean;
}

@Injectable({ providedIn: 'root' })
export class Game {
  private http    = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  startGame() {
    return this.http.post<GameResponse>(`${this.baseUrl}/game`, {});
  }

  makeGuess(gameId: number, guess: string) {
    return this.http.post<GuessResponse>(`${this.baseUrl}/game/${gameId}/guess`, { guess });
  }

  getGame(gameId: number) {
    return this.http.get<GameResponse>(`${this.baseUrl}/game/${gameId}`);
  }

  getHistory() {
    return this.http.get<GameResponse[]>(`${this.baseUrl}/game/history`);
  }
}
