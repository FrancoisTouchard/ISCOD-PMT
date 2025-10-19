import { Contributeur } from './contributeur.model';

export interface Projet {
  id: string;
  nom: string;
  description: string;
  dateDebut: string;
  contributeurs: Contributeur[];
}
