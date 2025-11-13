import { Project } from '../models/project.model';
import { Role } from '../models/role.enum';

/**
 * Récupère le rôle de l'utilisateur actuel dans un projet
 * @param project Le projet dont on veut connaître le rôle
 * @param userId L'ID de l'utilisateur connecté
 * @returns Le rôle de l'utilisateur ou null s'il n'est pas contributeur
 */
export function getCurrentUserRole(
  project: Project | null,
  userId: string
): Role | null {
  if (!project || !userId) return null;

  const contributor = project.contributors.find((c) => c.id.idUser === userId);

  return contributor?.role || null;
}
