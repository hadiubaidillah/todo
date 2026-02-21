import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskDTO } from '../models/task.model';
import { environment } from '../../../../environments/environment';

interface ParseTaskRequest {
  text: string;
  timezoneOffset: string;
}

@Injectable({ providedIn: 'root' })
export class AiTaskService {
  private readonly baseUrl = `${environment.apiGatewayUrl}/ai`;

  constructor(private http: HttpClient) {}

  parseTasks(text: string): Observable<TaskDTO[]> {
    const offset = -new Date().getTimezoneOffset();
    const h = Math.floor(Math.abs(offset) / 60).toString().padStart(2, '0');
    const m = (Math.abs(offset) % 60).toString().padStart(2, '0');
    const timezoneOffset = `${offset >= 0 ? '+' : '-'}${h}:${m}`;

    const request: ParseTaskRequest = { text, timezoneOffset };
    return this.http.post<TaskDTO[]>(`${this.baseUrl}/tasks/parse`, request);
  }
}
