import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UID } from '../../constants/api.constants';
import { UserService } from '../../services/user-service';
import { User } from '../../models/user/user-module';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
})
export class Profile implements OnInit {

  userId: string | null = null;
  user: User | null = null;
  errorMessage: string | null = null;

  constructor(private router: ActivatedRoute, private userService: UserService) { }

  ngOnInit(): void {
    this.userId = this.router.snapshot.paramMap.get(UID);
    console.log('User Id: ', this.userId);

    if (this.userId) {
      this.userService.getUserDetails(this.userId).subscribe({
        next: user => {
          console.log("User profile: ", user);
          this.user = user;
        },
        error: err => {
          this.errorMessage = err.message; // Show friendly message in UI
        }
      });
    }

  }

}
