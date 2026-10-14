import { animate, style, transition, trigger } from '@angular/animations';

export const routeFade = trigger('routeFade', [
  transition(':enter', [style({ opacity: 0, transform: 'translateY(10px)' }), animate('260ms cubic-bezier(.2,.7,.2,1)', style({ opacity: 1, transform: 'translateY(0)' }))])
]);

export const riseIn = trigger('riseIn', [
  transition(':enter', [style({ opacity: 0, transform: 'translateY(14px)' }), animate('360ms cubic-bezier(.2,.7,.2,1)', style({ opacity: 1, transform: 'translateY(0)' }))])
]);
