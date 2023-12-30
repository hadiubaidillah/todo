import { Spinner } from '@chakra-ui/react';
import { PropsWithChildren, Suspense } from 'react';
import ErrorBoundary from './ErrorBondary'

interface SuspenseWithBoundaryProps extends PropsWithChildren {
  fallback?: React.ReactNode;
  error?: React.ReactNode;
}
 
const SuspenseWithBoundary: React.FC<SuspenseWithBoundaryProps> = (props) => {
  return (
    <ErrorBoundary fallback={props.error}>
      <Suspense fallback={props.fallback || <Spinner mt={'10% !important'} alignSelf={'center'} size={'xl'}/>}>
        {props.children}
      </Suspense>
    </ErrorBoundary>
  );
}
 
export default SuspenseWithBoundary;