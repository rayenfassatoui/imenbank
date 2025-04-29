export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  username: string;
  roles: string[];
  expiresIn: number;
}

export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  role: string;
  active: boolean;
} 