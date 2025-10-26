import { Role } from '../models/role.enum';

export function getRoleString(role: Role): string {
  switch (role) {
    case Role.ADMINISTRATEUR:
      return 'Administrateur';
    case Role.MEMBRE:
      return 'Membre';
    case Role.OBSERVATEUR:
      return 'Observateur';
    default:
      return role;
  }
}
