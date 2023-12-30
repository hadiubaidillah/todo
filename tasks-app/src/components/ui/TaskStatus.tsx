import { useEffect, useState } from 'react';
import dayjs from 'dayjs';
import { Spinner, Stack, Text, Tooltip } from '@chakra-ui/react';

interface TaskStatusProps {
  createdAt: Date;
  ends: Date;
}

const TaskStatus: React.FC<TaskStatusProps> = (props) => {
  const [running, setRunning] = useState<boolean>();

  useEffect(() => {
    const diff = dayjs(props.ends).diff(dayjs.utc(), 'milliseconds');
    if (diff <= 0) {
      setRunning(false);
      return;
    }
    const timeout = setTimeout(() => {
      setRunning(false);
    }, diff);

    setRunning(true);
    return () => clearTimeout(timeout);
  }, [props.ends]);

  return (
    <Tooltip label={`Task created in ${dayjs(props.createdAt).format('LLL')}`}>
      {running == null ? (
        <Stack bg={'green.400'} px={'1'} rounded={8}>
          <Spinner color={'yellow.500'} bg={'green.400'} px={'1'} rounded={8} size={'sm'} />
        </Stack>
      ) : running ? (
        <Text fontFamily={'Bebas Neue'} color={'white'} bg={'green.400'} px={'1'} rounded={8} fontSize={'sm'}>
          In progress
        </Text>
      ) : (
        <Text fontFamily={'Bebas Neue'} color={'white'} bg={'gray.400'} px={'1'} rounded={8} fontSize={'sm'}>
          Concluded
        </Text>
      )}
    </Tooltip>
  );
};

export default TaskStatus;
