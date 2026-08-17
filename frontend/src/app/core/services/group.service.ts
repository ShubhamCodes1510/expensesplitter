import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

export interface Group {
  id?: number;
  name: string;
  description?: string;
  inviteCode?: string;
  createdById?: number;
  createdByName?: string;
  members?: User[];
  defaultCategoryId?: number;
  defaultCategoryName?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  memberCount?: number;
}

@Injectable({
  providedIn: 'root',
})
export class GroupService {
  private apiUrl = `${environment.apiUrl}/groups`;

  constructor(private http: HttpClient) {}

  createGroup(group: { name: string; description?: string }, userId: number): Observable<Group> {
    return this.http.post<Group>(`${this.apiUrl}?userId=${userId}`, group);
  }

  getUserGroups(userId: number): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.apiUrl}/user/${userId}`);
  }

  getGroupById(id: number): Observable<Group> {
    return this.http.get<Group>(`${this.apiUrl}/${id}`);
  }

  getGroupByInviteCode(inviteCode: string): Observable<Group> {
    return this.http.get<Group>(`${this.apiUrl}/join/${inviteCode}`);
  }

  joinGroup(inviteCode: string, userId: number): Observable<Group> {
    return this.http.post<Group>(`${this.apiUrl}/join?inviteCode=${inviteCode}&userId=${userId}`, {});
  }

  addMember(groupId: number, userId: number): Observable<Group> {
    return this.http.post<Group>(`${this.apiUrl}/${groupId}/members?userId=${userId}`, {});
  }

  addMemberByUsername(groupId: number, username: string): Observable<Group> {
    return this.http.post<Group>(`${this.apiUrl}/${groupId}/members/by-username?username=${username}`, {});
  }

  removeMember(groupId: number, userId: number): Observable<Group> {
    return this.http.delete<Group>(`${this.apiUrl}/${groupId}/members/${userId}`);
  }

  updateGroup(id: number, group: { name?: string; description?: string }): Observable<Group> {
    return this.http.put<Group>(`${this.apiUrl}/${id}`, group);
  }

  deleteGroup(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
