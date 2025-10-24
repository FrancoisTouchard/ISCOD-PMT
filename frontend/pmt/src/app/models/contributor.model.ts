export interface ContributorId {
  idUser: string;
  idProject: string;
}

export interface Contributor {
  id: ContributorId;
  role: 'ADMINISTRATEUR' | 'MEMBRE' | 'OBSERVATEUR';
  userName: string;
  userEmail: string;
}
