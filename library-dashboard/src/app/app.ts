import { Component, computed, signal, SimpleChanges } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { progressLoading, ROUTES } from './constants/api.constants';
import { CustomSidenav } from "./component/custom-sidenav/custom-sidenav";
import { AuthService } from './service/auth-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    MatProgressSpinnerModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule, 
    CustomSidenav
    ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('library-dashboard');
  protected readonly loading = computed(() => { return progressLoading() });

  enableNav = signal(false);
  toggleSideNav = signal(true);

  constructor(private router: Router, private authService: AuthService) {
  }

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const path = event.urlAfterRedirects; // full path
        // console.log('Active path:', path);

        if (path === ROUTES.DASHBOARD) {
          this.enableNav.set(true);
        } else {
          this.enableNav.set(false);
        }
      }
    });
  }

  

  ngOnDestroy() {
    try{
      this.logout();
      console.log('Component destroyed');
    }catch(error){
      console.error('Error during logout:', error);
    }
  }


  logout(){
    this.authService.logout();
    this.router.navigate([ROUTES.LOGIN]);
  }

}
