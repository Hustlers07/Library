import { DataSource } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { map } from 'rxjs/operators';
import { Observable, BehaviorSubject, merge } from 'rxjs';
import { AuthService } from '../../services/auth-service';
import { User } from '../../models/user/user-module';

export class MembersViewDataSource extends DataSource<User> {
  private dataSubject = new BehaviorSubject<User[]>([]);
  paginator: MatPaginator | undefined;
  sort: MatSort | undefined;

  constructor(private users: User[]) {
    super();
    // Fetch users once and push into the subject
    this.updateData(this.users);
  }

  connect(): Observable<User[]> {
    if (this.paginator && this.sort) {
      return merge(
        this.dataSubject.asObservable(),
        this.paginator.page,
        this.sort.sortChange
      ).pipe(
        map(() => this.getPagedData(this.getSortedData([...this.dataSubject.value])))
      );
    } else {
      // Just return the data without paging/sorting until they're set
      return this.dataSubject.asObservable();
    }
  }

  disconnect(): void {
    this.dataSubject.complete();
  }

  private getPagedData(data: User[]): User[] {
    if (this.paginator) {
      const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
      return data.splice(startIndex, this.paginator.pageSize);
    }
    return data;
  }

  private getSortedData(data: User[]): User[] {
    if (!this.sort || !this.sort.active || this.sort.direction === '') {
      return data;
    }

    return data.sort((a, b) => {
      const isAsc = this.sort?.direction === 'asc';
      switch (this.sort?.active) {
        case 'name':
          return compare(a.fullName, b.fullName, isAsc);
        case 'id':
          return compare(+a.id, +b.id, isAsc);
        default:
          return 0;
      }
    });
  }

  get data(): User[] {
    return this.dataSubject.value;
  }

  updateData(users: User[]): void {
    this.dataSubject.next(users);
  }

}

function compare(a: string | number, b: string | number, isAsc: boolean): number {
  return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
