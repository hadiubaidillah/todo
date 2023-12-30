import { Stack, useDisclosure, VStack } from '@chakra-ui/react';

import Header from './components/Header';
import SuspenseWithBoundary from './components/layout/SuspenseWithBoundary';
import TodoLayout from './components/layout/TodoLayout';
import LoginModal from './components/LoginModal';
import InteractionBar from './components/ui/InteractionBar';
import TaskList from './components/ui/TaskList';
import CreateTaskModal from './components/CreateTaskModal';

import { customRequester, fetcher, poster } from './libs/fetch';

const App: React.FC = () => {
  const create = useDisclosure();

  return (
    <VStack gap={3} minH='100vh' w={'full'} bg={'gray.200'}>
      <Header fetcher={fetcher} putter={customRequester({ method: 'PUT' })} />
      <TodoLayout title={'To Do List'}>
        <InteractionBar onButtonPress={create.onOpen}/>
        <Stack>
          <SuspenseWithBoundary>
            <TaskList fetcher={fetcher} deleter={customRequester({ method: 'DELETE' })}/>
          </SuspenseWithBoundary>
        </Stack>
      </TodoLayout>
      <LoginModal />
      <CreateTaskModal 
        poster={poster} 
        isOpen={create.isOpen} 
        onClose={create.onClose}
      />
    </VStack>
  );
}

export default App;
