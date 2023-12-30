import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  Heading,
  ModalBody,
  ModalFooter,
  Button,
  ModalProps,
} from '@chakra-ui/react';
import { useKeycloak } from '@react-keycloak/web';

interface LoginModalProps extends Omit<ModalProps, 'isOpen' | 'onClose' | 'children'> {
}

const LoginModal: React.FC<LoginModalProps> = (props) => {
  const { keycloak } = useKeycloak();

  return (
    <Modal {...props} isCentered isOpen={!keycloak.authenticated} onClose={() => null}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>
          <Heading size={'md'}>You must be logged!</Heading>
        </ModalHeader>
        <ModalBody>To be able to use the services, log in with your account.</ModalBody>
        <ModalFooter>
          <Button onClick={() => keycloak.login({
            scope: 'authorities profile'
          })} colorScheme={'telegram'}>Login</Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
};

export default LoginModal;
