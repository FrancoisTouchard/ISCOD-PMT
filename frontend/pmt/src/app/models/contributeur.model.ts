export interface ContributeurId {
  idUtilisateur: string;
  idProjet: string;
}

export interface Contributeur {
  id: ContributeurId;
  role: 'ADMINISTRATEUR' | 'MEMBRE' | 'OBSERVATEUR';
}
