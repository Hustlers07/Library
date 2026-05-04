import { Component } from '@angular/core';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-members-view',
  imports: [],
  templateUrl: './members-view.html',
  styleUrl: './members-view.scss',
})
export class MembersView {

  constructor(private authService: AuthService) { }

  ngOnInit() {
    console.log('MembersView');
    
    this.authService.fetchUsers().subscribe(users => {
      console.log('Fetched users:', users);
    });
  }
}
