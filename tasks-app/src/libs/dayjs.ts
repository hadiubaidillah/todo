import dayjs from 'dayjs';
import calendar from 'dayjs/plugin/calendar'
import utc from 'dayjs/plugin/utc'
import duration from 'dayjs/plugin/duration'
import relativeTime from 'dayjs/plugin/relativeTime'
import localizedFormat from 'dayjs/plugin/localizedFormat'
import timezone from 'dayjs/plugin/timezone'

dayjs.extend(calendar);
dayjs.extend(duration);
dayjs.extend(utc);
dayjs.extend(localizedFormat);
dayjs.extend(relativeTime);
dayjs.extend(timezone);

dayjs.tz.setDefault('Asia/Jakarta');