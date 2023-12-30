import { IconButton, IconButtonProps, Input, InputGroup, InputLeftAddon, Stack, Tooltip } from '@chakra-ui/react';
import { faPlus, faSearch } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

interface SearchbarProps {
  onButtonPress?: IconButtonProps['onClick']
}

const InteractionBar: React.FC<SearchbarProps> = (props) => {
  return (
    <Stack w={'full'} alignItems={'center'}>
      <InputGroup maxW={'500px'} bg={'white'}>
        <InputLeftAddon bg={'white'} color={'black'}>
          <FontAwesomeIcon icon={faSearch} />
        </InputLeftAddon>
        <Tooltip label={'The search bar is disabled.'}>
          <Input isDisabled fontFamily={'Barlow'} placeholder={'Search for a task'} />
        </Tooltip>
        <Tooltip label={'Create a task'} hasArrow>
          <IconButton
            colorScheme={'telegram'} 
            aria-label='Search for a task'
            onClick={props.onButtonPress}
            icon={<FontAwesomeIcon icon={faPlus}/>}
            ml={1}
          />
        </Tooltip>
      </InputGroup>
    </Stack>
  );
};

export default InteractionBar;
