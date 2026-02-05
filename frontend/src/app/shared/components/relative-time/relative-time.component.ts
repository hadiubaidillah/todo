import { Component, input, OnInit, OnDestroy, signal } from '@angular/core';

@Component({
  selector: 'app-relative-time',
  standalone: true,
  template: `<span>{{ displayText() }}</span>`,
})
export class RelativeTimeComponent implements OnInit, OnDestroy {
  targetDate = input.required<string | Date>();

  displayText = signal<string>('');

  private intervalId: ReturnType<typeof setInterval> | null = null;

  ngOnInit(): void {
    this.updateRelativeTime();
    // Update every second for live effect
    this.intervalId = setInterval(() => this.updateRelativeTime(), 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }

  private updateRelativeTime(): void {
    const target = new Date(this.targetDate()).getTime();
    const now = Date.now();
    const diff = now - target; // positive = past, negative = future

    if (diff < 0) {
      // Future date
      this.displayText.set('in ' + this.formatDuration(Math.abs(diff)));
    } else {
      // Past date
      this.displayText.set(this.formatDuration(diff) + ' ago');
    }
  }

  private formatDuration(ms: number): string {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const weeks = Math.floor(days / 7);
    const months = Math.floor(days / 30);

    // Show the most significant unit with appropriate granularity
    if (months > 0) {
      return months === 1 ? '1 month' : `${months} months`;
    }
    if (weeks > 0) {
      return weeks === 1 ? '1 week' : `${weeks} weeks`;
    }
    if (days > 0) {
      const h = hours % 24;
      if (days >= 7) {
        return `${days} days`;
      }
      return h > 0 ? `${days}d ${h}h` : `${days} days`;
    }
    if (hours > 0) {
      const m = minutes % 60;
      return m > 0 ? `${hours}h ${m}m` : `${hours} hours`;
    }
    if (minutes > 0) {
      const s = seconds % 60;
      return s > 0 ? `${minutes}m ${s}s` : `${minutes} minutes`;
    }
    return seconds === 1 ? '1 second' : `${seconds} seconds`;
  }
}
