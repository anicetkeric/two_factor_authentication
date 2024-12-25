export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com/api',
  otpTimeout: 30, // seconds
  otpLength: 6,
  features: {
    enableMockAuth: false,
    enable2FA: true,
    enablePasswordReset: true,
  }
}; 