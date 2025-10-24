import { Contributor } from './contributor.model';

export interface LocalProject {
  name: string;
  description: string;
  startDate: string;
}

export interface Project extends LocalProject {
  id: string;
  contributors: Contributor[];
}
