export const environment = {
  production: false,
  apiUrl: 'http://localhost:8099/api',
  otpTimeout: 30, // seconds
  otpLength: 6,
  mockOtpCode: '123456', // Only for development
  features: {
    enableMockAuth: true,
    enable2FA: true,
    enablePasswordReset: true,
  }
}; 