import { Image, Spinner, VStack } from '@chakra-ui/react';
import logo from '../../logo.svg';

interface LoadingComponentProps {}
 
const LoadingComponent: React.FC<LoadingComponentProps> = () => {
  return (
    <VStack justifyContent={'center'} bg={'gray.300'} w={'full'} minH={'100vh'}>
      <Image alt={'logo'} src={logo} w={'256px'}/>
      <Spinner size={'lg'}/>
    </VStack>
  );
}
 
export default LoadingComponent;