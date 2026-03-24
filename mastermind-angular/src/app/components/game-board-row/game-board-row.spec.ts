import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameBoardRow } from './game-board-row';

describe('GameBoardRow', () => {
  let component: GameBoardRow;
  let fixture: ComponentFixture<GameBoardRow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameBoardRow],
    }).compileComponents();

    fixture = TestBed.createComponent(GameBoardRow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
