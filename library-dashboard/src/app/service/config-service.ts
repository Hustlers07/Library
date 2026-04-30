import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private env: any = {};

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(this.platformId)) {
      this.env = (window as any).__env || {};
    }
  }

  get apiUrl(): string {
    return this.env.apiUrl || 'http://localhost:3000';
  }
}