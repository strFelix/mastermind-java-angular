import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Dashboard } from './pages/dashboard/dashboard';
import { Game } from './pages/game/game';
import { Ranking } from './pages/ranking/ranking';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'game',      component: Game,      canActivate: [authGuard] },
  { path: 'ranking',   component: Ranking,   canActivate: [authGuard] },
];
