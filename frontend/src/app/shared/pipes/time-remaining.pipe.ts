import { Pipe, PipeTransform } from '@angular/core';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);

@Pipe({
  name: 'timeRemaining',
  standalone: true,
})
export class TimeRemainingPipe implements PipeTransform {
  transform(value: string | Date | null | undefined): string {
    if (!value) return '';
    const target = dayjs(value);
    const now = dayjs();

    if (target.isBefore(now)) {
      return 'Overdue';
    }

    return target.fromNow(true); // true = without "in" prefix → "7 days", "18 hours"
  }
}
