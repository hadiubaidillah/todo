import { chakra, CloseButton, Heading, HStack, Text } from '@chakra-ui/react';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import RegressiveCount from './RegressiveCount';
import TaskStatus from './TaskStatus';

interface TaskCardProps {
  item: Task;
  onDeleteClick(id: string): void;
}

export type Task = {
  id: string;
  name: string;
  description?: string;
  author: TaskAuthor;
  created_at: string;
  ends_in: string;
  completed: boolean;
}

export type TaskAuthor = {
  [key: string]: any
}

const GridItemMotion = chakra(motion.div);

const TaskCard: React.FC<TaskCardProps> = (props) => {
  return (
    <GridItemMotion
      whileTap={{ scale: 1.05 }}
      boxShadow={'0px 0px 3px 0px #00000059'}
      gap={1}
      display={'flex'}
      flexDir={'column'}
      p={2}
      rounded={8}
      color={'black'}
      w='234px'
      maxH='188px'
      bg='whiteAlpha.800'
      position={'relative'}
      cursor={'pointer'}
      userSelect='none'
    >
      <HStack justifyContent={'space-between'} mb={2}>
        <Heading fontFamily={'Barlow'} textAlign={'center'} fontWeight={500} size={'md'}>
          {props.item.name}
        </Heading>
        <CloseButton onClick={() => props.onDeleteClick(props.item.id)} onPointerDownCapture={e => e.stopPropagation()}/>
      </HStack>
      <Text fontFamily={'Montserrat'} overflow={'hidden'} fontSize={'14px'}>
        {props.item.description || 'Without description :('}
      </Text>
      <HStack justifyContent={'space-between'} mt={2}>
        <TaskStatus createdAt={dayjs(props.item.created_at).toDate()} ends={dayjs(props.item.ends_in).toDate()}/>
        <RegressiveCount ends={dayjs(props.item.ends_in).toDate()}/>
      </HStack>
    </GridItemMotion>
  );
};

export default TaskCard;
