import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Injectable, OnInit } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { Router } from "@angular/router";
export interface UserToken {
  token: string;
  user: {
    email: string;
    firstName: string;
    lastName: string;
    address: string;
    phoneNumber: string;
    birthday: Date;
    about: string;
    profilePic: string;
    gender: string;
  };
}

@Injectable({ providedIn: "root" })
export class AuthService {
  token: String;
  authentication: Subject<boolean> = new Subject<boolean>();
  constructor(private http: HttpClient, private router: Router) {
    if (localStorage.getItem("token")) {
      this.isAuthenticated = true;
      this.authentication.next(this.isAuthenticated);
    }
  }

  private isAuthenticated: boolean = false;

  login(email: string, password: string) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=login",
        {
          email: email,
          password: password
        }
      )
      .toPromise()
      .then(data => {
        if (data.error) {
          console.log(data.error);
          return data.error;
        } else {
          localStorage.setItem("user", JSON.stringify(data.user));
          localStorage.setItem("token", JSON.stringify(data.token));
          this.isAuthenticated = true;
          this.authentication.next(this.isAuthenticated);
          return null;
        }
        this.router.navigate(["/events"]);
      });
  }

  signUp(
    email,
    password,
    firstName,
    lastName,
    address,
    phoneNumber,
    birthday,
    about,
    languages,
    profile_pic
  ) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=signup",
        {
          user: {
            email: email,
            first_name: firstName,
            last_name: lastName,
            address: address,
            phone_number: phoneNumber,
            birthday: birthday,
            password: password,
            about: about,
            languages: languages,
            gender: "Rather not Say",
            profile_pic: null
          }
        }
      )
      .toPromise()
      .then(data => {
        console.log(data);
        // if (data.error) {
        //   console.log(data.error);
        //   return;
        // }
        return this.login(email, password);
      });
  }

  logout() {
    const token = JSON.parse(localStorage.getItem("token"));

    return this.http
      .post(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=logout",
        {
          token: token
        }
      )
      .toPromise()
      .then(res => {
        localStorage.removeItem("user");
        localStorage.removeItem("token");
        this.isAuthenticated = false;
        this.authentication.next(this.isAuthenticated);
        this.router.navigate(["/"]);
      });
  }

  autoLogin() {
    const token = JSON.parse(localStorage.getItem("token"));
    if (token) {
      this.isAuthenticated = true;
    }
    this.authentication.next(this.isAuthenticated);
  }

  isAuth() {
    return this.isAuthenticated;
  }

  updateProfile(
    firstName,
    lastName,
    address,
    phoneNumber,
    birthday,
    about,
    languages,
    gender,
    profilePic
  ) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/util/file_upload?f=saveFile",
        {
          base64string: profilePic
        }
      )
      .toPromise()

      .then(imgLink => {
        return this.http
          .post<any>(
            "http://localhost:80/save_the_date/Controllers/usercontroller?f=editUser",
            {
              user: {
                first_name: firstName,
                last_name: lastName,
                address: address,
                phone_number: phoneNumber,
                birthday: birthday,
                about: about,
                languages: languages,
                gender: gender,
                profile_pic: imgLink
              }
            }
          )
          .toPromise();
      })
      .then(result => {
        if (result.error) {
          console.log(result.error);
        } else {
          localStorage.setItem("user", JSON.stringify(result.user));
        }
        this.router.navigate(["/events"]);
      });
  }
  getUser(email) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=getUser",
        {
          email: email
        }
      )
      .toPromise();
  }
}
