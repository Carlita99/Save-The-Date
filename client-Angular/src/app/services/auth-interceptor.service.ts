import { HttpInterceptor } from "@angular/common/http";
import { AuthService } from "./auth.service";
import { Injectable } from "@angular/core";
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}
  intercept(
    req: import("@angular/common/http").HttpRequest<any>,
    next: import("@angular/common/http").HttpHandler
  ) {
    console.log(req);
    // console.log("isAuth: " + this.authService.isAuth());
    if (!this.authService.isAuth()) {
      return next.handle(req);
    } else {
      const token = JSON.parse(localStorage.getItem("token"));
      const cloneReq = req.clone({
        setHeaders: {
          token: token
        }
      });
      console.log(cloneReq);
      return next.handle(cloneReq);
    }
  }
}
