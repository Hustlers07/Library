import { AfterViewInit, ChangeDetectorRef, Component, signal, ViewChild, WritableSignal } from '@angular/core';
import { MatTableModule, MatTable } from '@angular/material/table';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSortModule, MatSort } from '@angular/material/sort';
import { MembersViewDataSource } from './members-view-datasource';
import { AuthService } from '../../services/auth-service';
import { User } from '../../models/user/user-module';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { Router } from '@angular/router';
import { ROUTES, UID } from '../../constants/api.constants';

@Component({
  selector: 'app-members-view',
  templateUrl: './members-view.component.html',
  styleUrls: ['./members-view.component.scss'],
  imports: [MatTableModule, MatPaginatorModule, MatSortModule, MatProgressSpinner],
})
export class MembersViewComponent implements AfterViewInit {
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<User>;

  loading = signal(true);
  dataSource = new MembersViewDataSource([]);
  displayedColumns = ['uid', 'username', 'email'];

  constructor(private cdr: ChangeDetectorRef, private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.authService.fetchUsers().subscribe(users => {

      console.log("Users: ", users);

      this.loading.set(false);
      this.dataSource = new MembersViewDataSource(users);

      // Attach paginator/sort immediately if they’re already available
      if (this.paginator && this.sort) {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    setTimeout(() => {
      this.paginator.pageSize = 10;
      this.paginator.pageIndex = 0;
      this.cdr.detectChanges();
    });
  }

  onMembersRowClick(member: User):void {
    console.log('Method not implemented.', member);
    this.router.navigate([ROUTES.PROFILE.replace(":"+UID, member.username)])
  }

}
