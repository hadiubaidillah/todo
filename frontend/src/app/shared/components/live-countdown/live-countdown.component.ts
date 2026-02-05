import { Component, input, output, OnInit, OnDestroy, signal } from '@angular/core';

@Component({
  selector: 'app-live-countdown',
  standalone: true,
  template: `
    <span [class]="isOverdue() ? 'text-red-500' : 'text-surface-600'">
      {{ displayText() }}
    </span>
  `,
})
export class LiveCountdownComponent implements OnInit, OnDestroy {
  targetDate = input.required<string | Date>();
  showSeconds = input<boolean>(true);

  // Emit when status changes to overdue
  overdueChange = output<boolean>();

  displayText = signal<string>('');
  isOverdue = signal<boolean>(false);

  private intervalId: ReturnType<typeof setInterval> | null = null;
  private wasOverdue = false;

  ngOnInit(): void {
    this.updateCountdown();
    // Update every second for live countdown effect
    this.intervalId = setInterval(() => this.updateCountdown(), 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  private updateCountdown(): void {
    const target = new Date(this.targetDate()).getTime();
    const now = Date.now();
    const diff = target - now;

    const currentlyOverdue = diff <= 0;

    if (currentlyOverdue) {
      // Overdue - show how much time has passed
      this.isOverdue.set(true);
      this.displayText.set(this.formatDuration(Math.abs(diff)) + ' overdue');
    } else {
      // Time remaining
      this.isOverdue.set(false);
      this.displayText.set(this.formatDuration(diff) + ' left');
    }

    // Emit event when status changes to overdue
    if (currentlyOverdue !== this.wasOverdue) {
      this.wasOverdue = currentlyOverdue;
      this.overdueChange.emit(currentlyOverdue);
    }
  }

  private formatDuration(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    const s = seconds % 60;
    const m = minutes % 60;
    const h = hours % 24;

    const parts: string[] = [];

    if (days > 0) {
      parts.push(`${days}d`);
    }
    if (h > 0 || days > 0) {
      parts.push(`${h}h`);
    }
    if (m > 0 || h > 0 || days > 0) {
      parts.push(`${m}m`);
    }
    if (this.showSeconds()) {
      parts.push(`${s}s`);
    }

    return parts.join(' ') || '0s';
  }
}
