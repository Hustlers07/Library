import { Component, computed, signal } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterOutlet } from '@angular/router';
import { progressLoading } from './constants/api.constants';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    MatProgressSpinnerModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('library-dashboard');
  protected readonly loading = computed(() => {return progressLoading()});
}
