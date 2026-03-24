import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormFieldComponent } from "../../components/form-field/form-field";

@Component({
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, FormFieldComponent],
  templateUrl: './register.html',
  standalone: true,
})
export class Register {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  form = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  get f() { return this.form.controls; }

  onRegister() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    console.log("Registering with", this.form.value);

    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const { username, email, password } = this.form.value;

    this.auth.register(username!, email!, password!).subscribe({
      next: () => {
        this.successMessage.set('Conta criada! Redirecionando...');
        setTimeout(() => this.router.navigate(['/']), 2000);
      },
      error: (error) => {
        this.errorMessage.set(
          error.status === 409
            ? 'Usuário ou e-mail já cadastrado.'
            : 'Ocorreu um erro. Tente novamente mais tarde.'
        );
        this.loading.set(false);
        setTimeout(() => this.errorMessage.set(''), 5000);
      }
    });
  }
}
