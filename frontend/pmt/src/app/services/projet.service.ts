import { Injectable, OnDestroy } from '@angular/core';
import { Projet } from '../models/projet.model';
import { Subscription, tap } from 'rxjs';
import { ApiService } from './api.service';
import { User } from '../models/user.model';

const API_URL = 'http://localhost:8080/';

@Injectable({
  providedIn: 'root',
})
export class ProjetService implements OnDestroy {
  projets: Projet[] = [];
  private subscription?: Subscription;

  constructor(private apiService: ApiService) {}

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  getProjectsByContributeur(userId: string) {
    return this.apiService.getProjectsByContributeur(userId).pipe(
      tap((projets) => {
        this.projets = projets;
      })
    );
  }

  deleteProjectById(id: string) {
    return this.apiService.deleteProject(id).pipe(
      tap((projets) => {
        this.projets = projets;
      })
    );
  }
}
