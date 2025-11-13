import { Injectable, OnDestroy } from '@angular/core';
import { Observable, Subscription, tap } from 'rxjs';
import { ApiService } from './api.service';
import { Contributor } from '../models/contributor.model';
import { Role } from '../models/role.enum';

@Injectable({
  providedIn: 'root',
})
export class ContributorService implements OnDestroy {
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {}

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  addContributor(
    projectId: string,
    email: string,
    role: Role
  ): Observable<Contributor> {
    return this.apiService.postContributor(projectId, email, role);
  }

  deleteContributor(projectId: string, userId: string): Observable<void> {
    return this.apiService.deleteContributor(projectId, userId);
  }

  updateContributorRole(
    projectId: string,
    userId: string,
    newRole: Role
  ): Observable<Contributor> {
    return this.apiService.patchContributorRole(projectId, userId, newRole);
  }
}
