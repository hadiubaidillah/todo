export interface Notification {
  id: string;
  userId: string;
  sourceService: string;
  sourceId: string;
  type: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
}
