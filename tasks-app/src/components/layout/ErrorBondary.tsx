import React, { PropsWithChildren } from 'react'
import { Heading, HStack } from '@chakra-ui/react';


interface ErrorBoundaryProps extends PropsWithChildren {
  fallback?: React.ReactNode;
}

class ErrorBoundary extends React.Component<ErrorBoundaryProps, { hasError: boolean }> {

  constructor(props: any) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: any) {
    return { hasError: true };
  }

  render() {
    if (!this.state.hasError) {
      return this.props.children; 
    }
    
    return this.props.fallback || (<HStack justifyContent={'center'}>
      <Heading textAlign={'center'} size={'md'}>An error occurred while loading the content.</Heading>
    </HStack>);
  }
}

export default ErrorBoundary;