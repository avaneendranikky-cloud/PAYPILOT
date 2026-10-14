import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';

@Component({
  selector: 'pp-icon', standalone: true, imports: [CommonModule, FontAwesomeModule],
  template: '<fa-icon [icon]="icon" [fixedWidth]="true" aria-hidden="true"></fa-icon>'
})
export class IconComponent { @Input({ required: true }) icon!: IconDefinition; }
