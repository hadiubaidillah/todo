import { PopoverBody } from '@chakra-ui/react';
import NotificationCard, { Notification } from '../NotificationCard';

interface NotificationsListProps {
  data?: Notification[];
  putter(value: `/notifications/unreads/${string}`): Promise<unknown>;
  isLoading: boolean;
}

const NotificationsList: React.FC<NotificationsListProps> = (props) => {
  return (
    <PopoverBody
      overflow={'auto'}
      display={'flex'}
      flexDir={'column'}
      as={'ul'}
      gap={2}
      maxH={'180px'}
    >
      {props.data?.map((value) => (
        <NotificationCard putter={props.putter} key={value.id} item={value} />
      ))}
    </PopoverBody>
  );
};

export default NotificationsList;
