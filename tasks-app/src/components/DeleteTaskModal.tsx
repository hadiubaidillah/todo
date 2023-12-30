import React, { useImperativeHandle, useState }  from 'react';
import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  Text,
  ModalProps,
  useBoolean,
  useDisclosure,
} from '@chakra-ui/react';
import useMutation from 'swr/mutation';
import { useSWRConfig } from 'swr';

interface DeleteTaskModalProps extends Omit<ModalProps, 'children' | 'isOpen' | 'onClose'> {
  deleter(path: `/tasks/${string}`): Promise<unknown>
}

export interface DeleteTaskHandler {
  open(id: string): void;
}

const DeleteTaskModal = React.forwardRef<DeleteTaskHandler, DeleteTaskModalProps>((props, ref) => {
  const [id, setId] = useState('');
  const { isOpen, onClose, onOpen } = useDisclosure();
  const [isLoading, { on, off }] = useBoolean();
  
  const { trigger } = useMutation(`/tasks/${id}`, props.deleter);
  const { mutate } = useSWRConfig();

  useImperativeHandle(ref, () => ({
    open: (id: string) => {
      onOpen();
      setId(id);
    }
  }), []);

  return (
    <Modal {...props} isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          <Text>Are you sure you want to delete it?</Text>
        </ModalHeader>
        <ModalBody>
          You are about to delete a task, this task cannot be recovered after
          this act, do you confirm?
        </ModalBody>
        <ModalFooter>
          <Button 
            isLoading={isLoading} 
            loadingText={'Deleting'}
            colorScheme={'red'}
            onClick={() => {
              on();
              trigger().finally(() => {                
                off();
                mutate('/notifications/all');
                mutate('/tasks').finally(onClose);
              });
            }}
          >
            Confirm
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
});

export default DeleteTaskModal;
