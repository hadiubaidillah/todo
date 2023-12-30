import { useMemo } from 'react';
import { VStack, HStack, Heading, Text, IconButton, Tooltip } from '@chakra-ui/react';
import { faCheckDouble } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import dayjs from 'dayjs';
import useMutation from 'swr/mutation'

import { Task } from '../ui/TaskCard';
import { useSWRConfig } from 'swr';

interface NotificationCardProps {
  item: Notification;
  putter(value: `/notifications/unreads/${string}`): Promise<unknown>;
}


export type Notification = {
  created_at: string;
  id: string;
  readed: boolean;
  sent: boolean;
  task: Task;
  type: 'CREATION' | 'DELETION' |'FINISHED';
};

const NotificationCard: React.FC<NotificationCardProps> = ({ item, putter }) => {
  const { trigger } = useMutation(`/notifications/unreads/${item.id}`, putter);
  const { mutate } = useSWRConfig();
  const date = useMemo(() => dayjs(item.created_at), [item.created_at])

  return (
    <VStack as={'li'} p={2} bg={item.readed ? 'gray.100' : 'yellow.100'} alignItems={'stretch'} rounded={6}>
      <HStack alignItems={'flex-start'}>
        <Heading
          flex={1}
          lineHeight={'none'}
          fontFamily={'Montserrat'}
          fontWeight={500}
          size={'sm'}
        >
          {message(item.type) + '\n'}
          <Text fontFamily={'Barlow'}>{item.task.name}</Text>
        </Heading>
        <Tooltip isDisabled={item.readed} hasArrow label={'Mark as read'}>
          <IconButton 
            _hover={{ color: 'green' }}
            color={'gray'}
            aria-label='Mark as read'
            colorScheme={'unstyled'}
            isDisabled={item.readed}
            icon={<FontAwesomeIcon color={'inherit'} fontSize={16} icon={faCheckDouble} />}
            p={1}
            size={'xs'}
            onClick={() => {
              trigger().finally(() => mutate('/notifications/all'));
            }}
          />
        </Tooltip>
      </HStack>
      <Tooltip label={date.format('LLLL')}>
        <Text 
          color={'white'} 
          rounded={4} 
          alignSelf={'flex-start'} 
          bg={'gray.600'} 
          px={1} 
          fontFamily={'Barlow'} 
          fontWeight={500} 
          size={'sm'}
          userSelect={'none'}
        >
          {date.calendar(null, {
            sameDay: '[Today at] LT',
            nextDay: '[Tomorrow at] LT',
            nextWeek: 'dddd [at] LT',
            lastDay: '[Yesterday at] LT',
            sameElse: 'DD/MM/YYYY'
          })}
        </Text>
      </Tooltip>
    </VStack>
  );
};

function message(type: Notification['type']) {
  switch (type) {
    case 'CREATION':
      return 'Task created';
    case 'DELETION':
      return 'Task has been deleted';
    case 'FINISHED':
      return 'A task has been completed';
  }
}

export default NotificationCard;
