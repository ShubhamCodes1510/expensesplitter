import { Injectable, signal, effect } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private readonly STORAGE_KEY = 'app_theme';
  
  theme = signal<Theme>(this.getStoredTheme());

  constructor() {
    effect(() => {
      const currentTheme = this.theme();
      this.applyTheme(currentTheme);
      localStorage.setItem(this.STORAGE_KEY, currentTheme);
    });
  }

  private getStoredTheme(): Theme {
    const stored = localStorage.getItem(this.STORAGE_KEY) as Theme;
    if (stored) {
      return stored;
    }
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  private applyTheme(theme: Theme): void {
    document.body.classList.remove('light-theme', 'dark-theme');
    document.body.classList.add(`${theme}-theme`);
    document.documentElement.setAttribute('data-theme', theme);
  }

  toggleTheme(): void {
    this.theme.update((current) => (current === 'light' ? 'dark' : 'light'));
  }

  setTheme(theme: Theme): void {
    this.theme.set(theme);
  }

  isDarkMode(): boolean {
    return this.theme() === 'dark';
  }
}
