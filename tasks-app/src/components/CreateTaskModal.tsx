import { useEffect } from 'react';
import * as yup from 'yup'
import dayjs from 'dayjs';
import useMutation from 'swr/mutation'
import { Button, FormControl, FormErrorIcon, FormErrorMessage, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, ModalProps, Textarea } from '@chakra-ui/react';
import { yupResolver } from '@hookform/resolvers/yup'
import { useForm } from 'react-hook-form';

interface CreateTaskModalProps extends Omit<ModalProps, 'children'> {
  poster(path: '/tasks', args: { arg: any }): any;
}

const getSchema = () => yup.object({
  name: yup.string().min(4, 'Insert a title with more than 4 characters.'),
  description: yup.string().optional().nullable(),
  ends_in: yup.date()
    .typeError("Insira uma data valida")
    .min(new Date(), 'You cannot use dates/times smaller than now!')
    .max(dayjs().add(2, 'weeks').toDate(), 'You can only schedule tasks between now and 2 weeks.')
    .required('You need to fill in a date'),
  completed: yup.boolean().default(false).nullable()
});
 
const CreateTaskModal: React.FC<CreateTaskModalProps> = ({ poster, ...modalProps }) => {
  const { handleSubmit, register, formState: { errors } } = useForm<any>({
    resolver: yupResolver(getSchema())
  });
  const { trigger, reset, isMutating } = useMutation('/tasks', poster);

  const onSubmit = (e: any) => {
    trigger(e).then(modalProps.onClose);
  }

  useEffect(() => {
    reset();
  }, []);

  return (
    <Modal {...modalProps}>
      <ModalOverlay/>
      <ModalContent as={'form'} onSubmit={handleSubmit(onSubmit)}>
        <ModalHeader>
          Create a task
          <ModalCloseButton/>
        </ModalHeader>
        <ModalBody>
          <FormControl isInvalid={!!errors?.name}>
            <FormLabel>Title</FormLabel>
            <Input {...register('name')} placeholder='Insert a Title'/>
            <FormErrorMessage>
              <FormErrorIcon/>
              {(errors?.name?.message?.toString()) || ''}
            </FormErrorMessage>
          </FormControl>
          <FormControl>
            <FormLabel>Description</FormLabel>
            <Textarea {...register('description')} placeholder='Insert a description'/>
          </FormControl>
          <FormControl isInvalid={!!errors?.ends_in}>
            <FormLabel>End date</FormLabel>
            <Input 
              min={dayjs().format('YYYY-MM-DDThh:mm')} 
              max={dayjs().add(14, 'days').format('YYYY-MM-DDThh:mm')}
              type={'datetime-local'}
              {...register('ends_in')}
            />
            <FormErrorMessage>
              <FormErrorIcon/>
              {(errors?.ends_in?.message?.toString()) || ''}
            </FormErrorMessage>
          </FormControl>
        </ModalBody>
        <ModalFooter gap={2}>
          <Button colorScheme={'blackAlpha'}>Cancel</Button>
          <Button isLoading={isMutating} name={'criar'} type='submit' aria-label='Create a task' colorScheme={'telegram'}>Create</Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
}
 
export default CreateTaskModal;