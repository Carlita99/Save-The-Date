import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
  CanActivate
} from "@angular/router";
import { Injectable } from "@angular/core";
import { AuthService } from "./auth.service";
@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ):
    | boolean
    | import("@angular/router").UrlTree
    | import("rxjs").Observable<boolean | import("@angular/router").UrlTree>
    | Promise<boolean | import("@angular/router").UrlTree> {
    if (this.authService.isAuth()) {
      return true;
    } else {
      this.router.navigate(["/"], { queryParams: { login: true } });
    }
  }
}
