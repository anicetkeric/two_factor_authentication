import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../model/api-response';
import { UserResponse } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/user`;
  private readonly httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  getCurrentUser(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiUrl}/me`, this.httpOptions);
  }

  updateProfile(data: {
    username?: string;
    bio?: string;
  }): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(`${this.apiUrl}/profile`, data, this.httpOptions);
  }

  updatePassword(data: {
    currentPassword: string;
    newPassword: string;
  }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/change-password`, data, this.httpOptions);
  }

  enable2FA(code: string): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiUrl}/totp/enable?code=${code}`, {}, this.httpOptions);
  }
  generate2FAQRCode(): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiUrl}/totp/generate`, {}, this.httpOptions);
  }

  verify2FA(code: string): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiUrl}/totp/verify?code=${code}`, this.httpOptions);
  }

  disable2FA(): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiUrl}/totp/disable`, {}, this.httpOptions);
  }

  getRecoveryCodes(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiUrl}/totp/recovery-codes`, this.httpOptions);
  }

  generateNewRecoveryCodes(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiUrl}/totp/recovery-codes/generate`, {}, this.httpOptions);
  }
} 