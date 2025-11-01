import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root',
})

// Service permettant d'afficher les messages d'erreurs venant du backend, ou un fallback approprié selon le contexte
export class ErrorHandlerService {
  constructor(private toastService: ToastService) {}

  handleError(err: HttpErrorResponse, fallbackMessage: string): void {
    console.error(fallbackMessage, err);
    const message = err.error?.message || fallbackMessage;
    this.toastService.showToast(message, 'error');
  }
}
