import React from 'react';
import { PopoverTrigger, Button, Spinner, Stack } from '@chakra-ui/react';
import { faBell } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

interface NotificationsTriggerProps {
  hasUnreads: boolean;
  isLoading: boolean;
}

const NotificationsTrigger: React.FC<NotificationsTriggerProps> = (props) => {
  return (
    <PopoverTrigger>
      <Button
        aria-label='Notification'
        w={'38px'}
        h={'38px'}
        p={2}
        rounded={19}
        colorScheme={'green'}
      >
        <FontAwesomeIcon color='white' fontSize={24} icon={faBell} />
        <NotificationIndicator {...props} />
      </Button>
    </PopoverTrigger>
  );
};

interface NotificationIndicatorProps {
  isLoading: boolean;
  hasUnreads: boolean;
}

const NotificationIndicator: React.FC<NotificationIndicatorProps> = ({ isLoading, hasUnreads }) => {
  if (isLoading) {
    return (
      <Spinner
        position={'absolute'}
        top={'6px'}
        right={'6px'}
        size='xs'
        color={'red.600'}
      />
    );
  }

  if (hasUnreads) {
    return (
      <Stack
        bg={'red'}
        rounded={8}
        position={'absolute'}
        top={'5px'}
        right={1}
        w={'8px'} h={'8px'}
      />
    );
  }
  return <React.Fragment />;
};

export default NotificationsTrigger;
