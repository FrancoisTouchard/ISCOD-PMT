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

  updateContributorRole(
    projectId: string,
    userId: string,
    newRole: Role
  ): Observable<Contributor> {
    return this.apiService.patchContributorRole(projectId, userId, newRole);
  }
}
