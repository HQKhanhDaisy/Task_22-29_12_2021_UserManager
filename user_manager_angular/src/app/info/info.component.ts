import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpServiceService } from 'src/service/http-service.service';
import { User } from '../model/user';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { UtilsService } from 'src/service/utils.service';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css'],
})
export class InfoComponent implements OnInit {
  public formUpdate: FormGroup = new FormGroup({
    username: new FormControl(''),
    firstname: new FormControl('', [Validators.required]),
    lastname: new FormControl('', [Validators.required]),
    email: new FormControl('', [
      Validators.required,
      Validators.pattern('^[a-z0-9](.?[a-z0-9]){0,}@g(oogle)?mail.com$'),
    ]),
    role: new FormControl(''),
  });
  public username!: string;
  public user: User = new User('');
  public submitted: boolean = false;
  error: any = '';
  constructor(
    private router: ActivatedRoute,
    private httpService: HttpServiceService,
    private messageError: UtilsService
  ) {}

  ngOnInit(): void {
    this.username = this.router.snapshot.params['username'];
    this.httpService.getUserByUsername(this.username).subscribe((data) => {
      this.user.username = data.username;
      this.user.firstname = data.firstname;
      this.user.lastname = data.lastname;
      this.user.email = data.email.toLowerCase();
      this.user.password = data.password;
      this.user.roleName = data.roleName;
      this.formUpdate = new FormGroup({
        username: new FormControl(this.user.username),
        firstname: new FormControl(this.user.firstname, [Validators.required]),
        lastname: new FormControl(this.user.lastname, [Validators.required]),
        email: new FormControl(this.user.email, [
          Validators.required,
          Validators.pattern('^[a-z0-9](.?[a-z0-9]){0,}@g(oogle)?mail.com$'),
        ]),
        role: new FormControl(this.user.roleName),
      });
    });
  }
  public update() {
    this.submitted = true;
    if (this.formUpdate.valid) {
      this.user.username = this.formUpdate.value.username;
      this.user.firstname = this.formUpdate.value.firstname;
      this.user.lastname = this.formUpdate.value.lastname;
      this.user.email = this.formUpdate.value.email.toLowerCase();
      this.user.roleName = this.formUpdate.value.role;
      this.httpService.update(this.user).subscribe((data: any) => {
        if (data.statusCode) {
          this.error = this.messageError.messageError(data.statusCode);
        } else {
          alert('Successful update!');
          this.error = '';
        }
      });
    }
  }
  showRole(rolename: string) {
    if (rolename === 'ROLE_ADMIN') {
      return 'Admin';
    } else {
      return 'User';
    }
  }
  public goBack() {
    history.back();
  }
}
