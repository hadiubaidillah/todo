import { Component, input, OnDestroy, OnInit, signal } from '@angular/core';

@Component({
  selector: 'app-countdown',
  standalone: true,
  template: `
    @if (remaining()) {
      <span class="countdown">{{ remaining() }}</span>
    }
  `,
  styles: [`
    .countdown {
      font-size: 12px;
      color: #f44336;
      font-weight: 500;
    }
  `],
})
export class CountdownComponent implements OnInit, OnDestroy {
  targetDate = input.required<string>();
  remaining = signal<string>('');
  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnInit(): void {
    this.updateCountdown();
    this.intervalId = setInterval(() => this.updateCountdown(), 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) clearInterval(this.intervalId);
  }

  private updateCountdown(): void {
    const target = new Date(this.targetDate()).getTime();
    const now = Date.now();
    const diff = target - now;

    if (diff <= 0) {
      this.remaining.set('Expired');
      if (this.intervalId) clearInterval(this.intervalId);
      return;
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    const parts: string[] = [];
    if (days > 0) parts.push(`${days}d`);
    if (hours > 0) parts.push(`${hours}h`);
    if (minutes > 0) parts.push(`${minutes}m`);
    parts.push(`${seconds}s`);
    this.remaining.set(parts.join(' '));
  }
}
