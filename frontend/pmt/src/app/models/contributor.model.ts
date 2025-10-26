import { Role } from './role.enum';

export interface ContributorId {
  idUser: string;
  idProject: string;
}

export interface Contributor {
  id: ContributorId;
  role: Role;
  userName: string;
  userEmail: string;
}
