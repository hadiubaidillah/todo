import ReactDOM from 'react-dom/client';
import { ChakraProvider } from '@chakra-ui/react';
import { ReactKeycloakProvider } from '@react-keycloak/web';

import LoadingComponent from './components/layout/LoadingComponent';
import App from './App';
import { keycloak } from './libs/fetch';
import './libs/dayjs';

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <ChakraProvider>
    <ReactKeycloakProvider LoadingComponent={<LoadingComponent />} authClient={keycloak}>
      <App />
    </ReactKeycloakProvider>
  </ChakraProvider>
);
