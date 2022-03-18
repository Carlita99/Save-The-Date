import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { HomeEventsComponent } from "./home-events/home-events.component";
import { LandingPageComponent } from "./landing-page/landing-page.component";
import { LoginComponent } from "./login/login.component";
import { SignUpComponent } from "./sign-up/sign-up.component";
import { EditProfileComponent } from "./edit-profile/edit-profile.component";
import { SingleEventComponent } from "./single-event/single-event.component";
import { EditEventComponent } from "./edit-event/edit-event.component";
import { AddEventComponent } from "./add-event/add-event.component";
import { AddServiceComponent } from "./add-service/add-service.component";
import { EditServiceComponent } from "./edit-service/edit-service.component";
import { SingleServiceComponent } from "./single-service/single-service.component";
import { ServiceListComponent } from "./service-list/service-list.component";
import { ReservationsComponent } from "./reservations/reservations.component";
import { AboutUsComponent } from "./about-us/about-us.component";
import { ProfileComponent } from "./profile/profile.component";
import { AuthGuard } from "./services/auth-guard.service";

const routes: Routes = [
  {
    path: "profile/:email",
    component: ProfileComponent
  },
  {
    path: "aboutUs",
    component: AboutUsComponent
  },
  {
    path: "my-service-res/:id",
    canActivate: [AuthGuard],

    component: ReservationsComponent
  },
  {
    path: "service/:id",
    component: SingleServiceComponent
  },
  {
    path: "services-list/:email",
    component: ServiceListComponent
  },
  {
    path: "edit-service/:id",
    canActivate: [AuthGuard],
    component: EditServiceComponent
  },
  {
    path: "edit-profile",
    canActivate: [AuthGuard],

    component: EditProfileComponent
  },
  {
    path: "add-service",
    canActivate: [AuthGuard],

    component: AddServiceComponent
  },
  {
    path: "add-event",
    canActivate: [AuthGuard],

    component: AddEventComponent
  },
  {
    path: "edit-event/:id",
    canActivate: [AuthGuard],

    component: EditEventComponent
  },
  {
    path: "event/:id",
    component: SingleEventComponent
  },
  {
    path: "edit-profile",
    canActivate: [AuthGuard],
    component: EditProfileComponent
  },
  {
    path: "events",
    component: HomeEventsComponent
  },
  {
    path: "login",
    component: LoginComponent
  },
  {
    path: "signUp",
    component: SignUpComponent
  },
  {
    path: "",
    component: LandingPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
