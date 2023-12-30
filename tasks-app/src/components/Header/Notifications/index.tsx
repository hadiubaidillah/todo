import { useCallback, useMemo } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import useSWR from 'swr';
import useMutation from 'swr/mutation';

import {
  PopoverContent,
  PopoverArrow,
  PopoverFooter,
  Popover,
} from '@chakra-ui/react';

import { Notification } from '../NotificationCard';
import NotificationsTrigger from './NotificationsTrigger';
import NotificationsHeader from './NotificationsHeader';
import NotificationsList from './NotificationsList';

interface NotificationsProps {
  fetcher(value: '/notifications/all'): Promise<Notification[]>;
  putter(value: '/notifications/unreads' 
    | `/notifications/unreads/${string}`): Promise<unknown>;
}

function useNotifications(enable: boolean, fetcher: NotificationsProps['fetcher'], putter: NotificationsProps['putter']) {
  const { data, isLoading, isValidating, mutate } = useSWR(enable ? '/notifications/all' : null, fetcher);
  const { trigger, isMutating } = useMutation('/notifications/unreads', putter);
  const unreads = useMemo(() => {
    return (data || []).filter((item) => !item.readed)
  }, [data]);

  const readAll = useCallback(() => {
    trigger().then(() => mutate(data!!.map(i => ({...i, readed: true}))));
  }, [data, trigger, mutate])

  return {
    data, 
    hasUnreads: unreads.length != 0,
    isLoading,
    isMutating,
    isValidating,
    readAll
  }
}

const Notifications: React.FC<NotificationsProps> = (props) => {
  const { keycloak: { authenticated } } = useKeycloak();
  const { data, readAll, ...states } = useNotifications(!!authenticated, props.fetcher, props.putter);

  return (
    <Popover>
      <NotificationsTrigger hasUnreads={states.hasUnreads} isLoading={states.isLoading || states.isValidating}/>
      <PopoverContent bg={'whiteAlpha.900'} backdropFilter={'auto'} backdropBlur={'5px'}>
        <NotificationsHeader 
          hasUnreads={states.hasUnreads} 
          isLoading={states.isMutating} 
          readAll={readAll}
        />
        <NotificationsList 
          data={data} 
          isLoading={states.isLoading}
          putter={props.putter}
        />
        <PopoverArrow />
        <PopoverFooter />
      </PopoverContent>
    </Popover>
  );
};

export default Notifications;
