import { Component, OnInit } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { NgForm } from "@angular/forms";
import { AuthService } from "../services/auth.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"]
})
export class LoginComponent implements OnInit {
  constructor(
    private activeModal: NgbActiveModal,
    private authService: AuthService,
    private router: Router
  ) {}
  private email: string;
  private password: string;
  private errorMessage: string = null;
  ngOnInit() {}
  close() {
    this.activeModal.close();
  }
  onSubmit(form: NgForm) {
    this.authService
      .login(form.value.email, form.value.password)
      .then(error => {
        if (error) {
          this.errorMessage = error.message;
        } else {
          this.activeModal.close();
          this.router.navigate(["/events"]);
        }
      });
  }
}
//
