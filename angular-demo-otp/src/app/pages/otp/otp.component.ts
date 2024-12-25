import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../../environments/environment';
import { UserService } from '@/app/services/user.service';

@Component({
  selector: 'app-otp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './otp.component.html',
  styleUrls: ['./otp.component.scss']
})
export class OtpComponent implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);
  private userService = inject(UserService);

  otpForm: FormGroup;
  remainingTime = environment.otpTimeout;
  timerInterval: any;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor() {
    this.otpForm = this.fb.group({
      code: ['', [
        Validators.required,
        Validators.pattern(new RegExp(`^\\d{${environment.otpLength}}$`))
      ]],
    });
  }

  ngOnInit() {
    this.startTimer();
  }

  ngOnDestroy() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  startTimer() {
    this.remainingTime = environment.otpTimeout;
    this.timerInterval = setInterval(() => {
      if (this.remainingTime > 0) {
        this.remainingTime--;
      } else {
        clearInterval(this.timerInterval);
      }
    }, 1000);
  }

  onSubmit() {
    if (this.otpForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const code = this.otpForm.get('code')?.value;

      this.userService.verify2FA(code).subscribe({
        next: (response) => {
          this.authService.setToken(response.data.accessToken);
          this.router.navigate(['/home']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error.message || 'Invalid verification code';
          this.otpForm.get('code')?.setErrors({ incorrect: true });
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    }
  }


} 