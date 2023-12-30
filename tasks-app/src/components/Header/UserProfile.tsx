import {
  Avatar,
  Button,
  Popover,
  PopoverArrow,
  PopoverBody,
  PopoverContent,
  PopoverTrigger,
  Spinner,
  Text,
} from '@chakra-ui/react';
import { useKeycloak } from '@react-keycloak/web';
import { useEffect, useState } from 'react';

interface UserProfileProps {}

const UserProfile: React.FC<UserProfileProps> = () => {
  const [profile, setProfile] = useState<any>();
  const { keycloak } = useKeycloak();

  useEffect(() => {
    if (keycloak.authenticated) {
      keycloak.loadUserInfo().then((user) => {
        setProfile(user);
      });
    }
  }, []);

  return (
    <Popover size={'sm'}>
      <PopoverTrigger>
        <Button
          display={'flex'}
          gap={2}
          cursor={'pointer'}
          color={'white'}
          rounded={19}
          p={1}
          fontWeight={400}
          userSelect={'none'}
          pr={2}
          colorScheme={'green'}
        >
          <Avatar size={'sm'} name='' />
          {!profile ? <Spinner mr={16} size={'sm'} /> : <Text fontFamily={'Montserrat'}>{profile?.name}</Text>}
        </Button>
      </PopoverTrigger>
      <PopoverContent w={'195px'}>
        <PopoverBody p={1}>
          <Button fontFamily={'Barlow'} onClick={() => keycloak.logout()} colorScheme={'red'} w={'full'}>
            Logout
          </Button>
        </PopoverBody>
        <PopoverArrow />
      </PopoverContent>
    </Popover>
  );
};

export default UserProfile;
