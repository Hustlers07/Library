import { Component, computed, signal } from '@angular/core';
import { MatList, MatNavList } from "@angular/material/list";
import { MatIcon } from "@angular/material/icon";
import { MatCard } from "@angular/material/card";
import { activeUser, MENU_ITEMS, MenuItem, ROUTES } from '../../constants/api.constants';
import { Router, RouterLink } from '@angular/router';
import { TitleCasePipe } from '@angular/common';
import { AuthService } from '../../service/auth-service';
import { User } from '../../models/user/user-module';
import { RolePipe } from "../../pipe/role-pipe";

@Component({
  selector: 'app-custom-sidenav',
  imports: [MatNavList, MatIcon, MatCard, RouterLink, TitleCasePipe, RolePipe],
  templateUrl: './custom-sidenav.html',
  styleUrl: './custom-sidenav.scss',
})
export class CustomSidenav {


  menuItems = signal<MenuItem[]>(MENU_ITEMS);
  protected readonly user = computed<User | null>(() => { return activeUser() });


  constructor(private authService: AuthService, private router: Router) {

  }


  ngOnInit() {

    if (this.authService.getToken() !== null) {
      this.authService.getProfile().subscribe(
        profile => {
          console.log('User profile:', profile);
          activeUser.set(profile);
        },
        error => {
          console.error('Error fetching profile:', error);
          this.authService.logout();
          this.router.navigate([ROUTES.LOGIN]);
          
        }
      );
    }
  }


}
