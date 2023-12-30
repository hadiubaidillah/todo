import { Spinner, Text, Tooltip } from '@chakra-ui/react';
import { Duration } from 'dayjs/plugin/duration'
import dayjs from 'dayjs'
import { useEffect, useRef } from 'react';

interface RegressiveCountProps {
  ends: Date;
}
 
const RegressiveCount: React.FC<RegressiveCountProps> = (props) => {
  const duration = useRef<Duration>(dayjs.duration(0))
  const ref = useRef<HTMLParagraphElement>(null)

  useEffect(() => {
    var diff = dayjs(props.ends).diff(dayjs.utc(), 'milliseconds');
    duration.current = dayjs.duration(diff, 'milliseconds')

    const countdown = setInterval(() => {
      const tempo = duration.current.asMilliseconds() - 1000;
      if (tempo <= 0) {
        ref.current!.textContent = dayjs(props.ends).format('L');
        clearInterval(countdown);
        return;
      }

      const ins = dayjs.duration(tempo, 'millisecond');
      duration.current = ins;

      if (ins.days() >= 1) {
        ref.current!.textContent = ins.humanize();
        return;
      }

      if (ins.hours() >= 1) {
        ref.current!.textContent = `${addZero(ins.hours())}h${addZero(ins.minutes())}:${addZero(ins.seconds())}`;
        return;
      }

      ref.current!.textContent = `${addZero(ins.minutes())}m${addZero(ins.seconds())}s`;
    }, 1000);

    return () => clearInterval(countdown)
  }, [props.ends]);

  return (
    <Tooltip label={<TooltipLabel ends={props.ends} duration={duration}/>}>
      <Text fontFamily={'Teko'} ref={ref}><Spinner size={'xs'}/></Text>
    </Tooltip>
  );
}

interface TooltipLabelProps extends RegressiveCountProps {
  duration: React.MutableRefObject<Duration>
}

function TooltipLabel(props: TooltipLabelProps) {
  if (props.duration.current.asSeconds() <= 1) {
    return <>{dayjs(props.ends).format('LLLL')}</>
  }
  return <>{`Ends in ${props.duration.current.humanize()}`}</>
}

function addZero(num: Number) {
  return num.toString().length == 1 ? "0" + num : num.toString()
}
 
export default RegressiveCount;