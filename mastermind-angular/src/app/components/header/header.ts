import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.html',
})
export class HeaderComponent {
  @Input() buttonLabel = '';
  @Input() buttonDanger = false;
  @Output() buttonClick = new EventEmitter<void>();
}
