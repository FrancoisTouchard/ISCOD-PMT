import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const ErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error) => {
      let message = 'Une erreur est survenue';

      if (error.status === 400) {
        message = error.error?.message || 'Requête invalide.';
      } else if (error.status === 401) {
        message = 'Authentification requise.';
      } else if (error.status === 403) {
        message = 'Accès refusé.';
      } else if (error.status === 404) {
        message = error.error?.message || 'Ressource non trouvée.';
      } else if (error.status >= 500) {
        message = 'Erreur interne du serveur.';
      }

      return throwError(() => ({ ...error, message }));
    })
  );
};
