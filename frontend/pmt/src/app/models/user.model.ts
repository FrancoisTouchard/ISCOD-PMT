export interface LocalUser {
  name: string;
  email: string;
  password: string;
}

export interface User extends LocalUser {
  id: string;
}
