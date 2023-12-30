import {
  Hide,
  HStack,
  Image,
} from '@chakra-ui/react';

import logo from '../../logo.svg';
import { Notification } from './NotificationCard';
import Notifications from './Notifications';
import UserProfile from './UserProfile';

interface HeaderProps {
  fetcher(value: '/notifications/all'): Promise<Notification[]>;
  putter(value: '/notifications/unreads' 
    | `/notifications/unreads/${string}`): Promise<unknown>;
}

const Header: React.FC<HeaderProps> = (props) => {
  return (
    <HStack
      zIndex={5}
      top={0}
      position={'sticky'}
      justifyContent={'center'}
      w={'full'}
      bg={'whiteAlpha.700'}
      backdropFilter={'auto'}
      backdropBlur={'5px'}
    >
      <HStack
        px={'3%'}
        w={'full'}
        justifyContent={{
          base: 'flex-end',
          sm: 'space-between'
        }}
        maxW={'1100px'}
        minH={20}
      >
        <Hide below='sm'>
          <Image w={'100px'} h={'full'} src={logo} />
        </Hide>
        <HStack gap={3}>
          <Notifications 
            fetcher={props.fetcher} 
            putter={props.putter}
          />
          <UserProfile/>
        </HStack>
      </HStack>
    </HStack>
  );
};

export default Header;
