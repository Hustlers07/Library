import { Component, signal } from '@angular/core';
import { MatList, MatNavList } from "@angular/material/list";
import { MatIcon } from "@angular/material/icon";
import { MatCard } from "@angular/material/card";
import { MENU_ITEMS, MenuItem } from '../../constants/api.constants';
import { RouterLink } from '@angular/router';
import { TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-custom-sidenav',
  imports: [MatNavList, MatIcon, MatCard, RouterLink, TitleCasePipe],
  templateUrl: './custom-sidenav.html',
  styleUrl: './custom-sidenav.scss',
})
export class CustomSidenav {


  menuItems = signal<MenuItem[]>(MENU_ITEMS);

}
