export interface UserLogin {
  email: string;
  password: string;
}

export interface UserRegister {
  email: string;
  password: string;
  passwordConfirmation: string;
  fullname: string;
}

export interface UserResponse {
  id: string;
  username: string;
  fullName: string;
  bio: string;
  roles: any;
  enabled: boolean;
  mfaEnabled: boolean;
}


export interface AuthenticationResponseDTO {
  accessToken: string;
  refreshToken: string;
  isMfa: boolean;
}

