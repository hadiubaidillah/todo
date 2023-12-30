import { useRef } from 'react';
import useSWR from 'swr';
import { HStack } from '@chakra-ui/react';
import { useKeycloak } from '@react-keycloak/web';

import TaskCard, { Task } from './TaskCard';
import DeleteTaskModal, { DeleteTaskHandler } from '../DeleteTaskModal';

interface TaskListProps {
  fetcher(path: '/tasks'): Promise<Task[]>;
  deleter(path: `/tasks/${string}`): Promise<unknown>;
}

const TaskList: React.FC<TaskListProps> = (props) => {
  const { keycloak: { authenticated } } = useKeycloak();
  const deleteRef = useRef<DeleteTaskHandler>(null);
  const { data } = useSWR(authenticated ? '/tasks' : null, props.fetcher, {
    suspense: true,
  });

  return (
    <HStack
      flexWrap={'wrap'}
      alignItems={'flex-start'}
      justifyItems={'center'}
      justifyContent={'center'}
      gap={'20px'}
    >
      {data?.map((task, i) => (
        <TaskCard
          key={task.id}
          item={task}
          onDeleteClick={(id) => deleteRef.current?.open(id)}
        />
      ))}
      <DeleteTaskModal deleter={props.deleter} ref={deleteRef} />
    </HStack>
  );
};
export default TaskList;
