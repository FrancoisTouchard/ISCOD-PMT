export interface LocalUser {
  nom: string;
  email: string;
  password: string;
}

export interface User extends LocalUser {
  id: string;
}
