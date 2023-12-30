import { Heading, VStack } from '@chakra-ui/react';

interface TodoLayoutProps {
  children: React.ReactNode
  title: string;
}
 
const TodoLayout: React.FC<TodoLayoutProps> = (props) => {
  return (
    <VStack p={4} px={'3%'} alignItems={'stretch'} w={'full'} maxW={'1100px'} gap={5}>
      <Heading fontFamily={'Barlow'} fontWeight={'300'} px={10}>{props.title}</Heading>
      {props.children}
    </VStack>
  );
}
 
export default TodoLayout;