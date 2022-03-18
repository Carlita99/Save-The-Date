import { Component, OnInit, OnDestroy, AfterViewChecked } from "@angular/core";
import {
  NgbModalOptions,
  NgbModal,
  ModalDismissReasons,
  NgbModalRef
} from "@ng-bootstrap/ng-bootstrap";
import { LoginComponent } from "src/app/login/login.component";
import { AuthService } from "src/app/services/auth.service";
import { Subscription } from "rxjs";
import { PlatformLocation } from "@angular/common";
import { Router, ActivatedRoute } from "@angular/router";
@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.css"]
})
export class HeaderComponent implements OnInit, OnDestroy, AfterViewChecked {
  ngAfterViewChecked(): void {}
  ngOnDestroy(): void {
    if (this.modal) this.modal.close();
  }
  user;
  param;
  modal: NgbModalRef;
  subscription: Subscription;
  public ngbModalOptions: NgbModalOptions = {
    keyboard: false,
    centered: true
  };
  isAuth: boolean;
  constructor(
    private route: ActivatedRoute,
    private modalService: NgbModal,
    private authService: AuthService,
    private router: Router
  ) {
    this.subscription = this.authService.authentication.subscribe(isAuth => {
      this.isAuth = isAuth;
      if (isAuth) {
        this.user = JSON.parse(localStorage.getItem("user"));
        this.param = "{user:'" + this.user.email + "'}";
        console.log(this.param);
      }
    });
    this.route.queryParams.subscribe(params => {
      const login = this.route.snapshot.queryParams.login;
      console.log(login);
      if (login == "true") {
        console.log("ok");
        this.showLogin();
      }
    });
  }
  navigateEvents() {
    this.router.navigate(["/events"], {
      queryParams: { user: this.user.email }
    });
  }
  navigateServices() {
    this.router.navigate(["/services-list", this.user.email]);
  }
  ngOnInit() {
    this.authService.autoLogin();
  }
  showLogin() {
    this.modal = this.modalService.open(LoginComponent, this.ngbModalOptions);
  }

  logout() {
    this.authService.logout();
  }
}
//this.modalService;
//   .open(content, { ariaLabelledBy: "modal-basic-title" })
//   .result.then(
//     result => {
//       this.closeResult = `Closed with: ${result}`;
//     },
//     reason => {
//       this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
//     }
//   );
