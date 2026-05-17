import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UID } from '../../constants/api.constants';

@Component({
  selector: 'app-profile',
  imports: [],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {

  userId: string | null = null;

  constructor(private router: ActivatedRoute){}

  ngOnInit(): void {
    this.userId = this.router.snapshot.paramMap.get(UID);
    console.log('User Id: ', this.userId);
  }

}
