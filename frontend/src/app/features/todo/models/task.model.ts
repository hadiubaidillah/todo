export interface Task {
  id: string;
  name: string;
  description?: string;
  completed: boolean;
  createdAt: string;
  endsIn?: string;
  author?: {
    id: string;
    email: string;
    firstName?: string;
    lastName?: string;
  };
}

export interface TaskDTO {
  name: string;
  description?: string;
  endsIn?: string;
}
