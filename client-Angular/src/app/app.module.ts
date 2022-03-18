import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { MatCardModule } from "@angular/material/card";
import {
  MatDatepickerModule,
  MatFormFieldModule,
  MatInputModule,
  MatNativeDateModule
} from "@angular/material";

import { LandingPageComponent } from "./landing-page/landing-page.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { HeaderComponent } from "./shared/header/header.component";
import { HomeEventsComponent } from "./home-events/home-events.component";
import { EventsService } from "./services/events.service";
import { SlideshowModule } from "ng-simple-slideshow";
import { EventCardComponent } from "./shared/event-card/event-card.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { LoginComponent } from "./login/login.component";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AuthService } from "./services/auth.service";
import { MatRadioModule } from "@angular/material/radio";
import { MatSelectModule } from "@angular/material";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { SignUpComponent } from "./sign-up/sign-up.component";
import { EditProfileComponent } from "./edit-profile/edit-profile.component";
import { AddEventComponent } from "./add-event/add-event.component";
import { AddServiceComponent } from "./add-service/add-service.component";
import { SingleEventComponent } from "./single-event/single-event.component";
import { SingleServiceComponent } from "./single-service/single-service.component";
import { EditEventComponent } from "./edit-event/edit-event.component";
import { EditServiceComponent } from "./edit-service/edit-service.component";
import { AuthInterceptor } from "./services/auth-interceptor.service";
import { ServiceListComponent } from "./service-list/service-list.component";
import { ReservationsComponent } from "./reservations/reservations.component";
import { ProfileComponent } from "./profile/profile.component";
import { AboutUsComponent } from "./about-us/about-us.component";
import { AuthGuard } from "./services/auth-guard.service";
@NgModule({
  declarations: [
    AppComponent,
    LandingPageComponent,
    HeaderComponent,
    HomeEventsComponent,
    EventCardComponent,
    LoginComponent,
    SignUpComponent,
    EditProfileComponent,
    AddEventComponent,
    AddServiceComponent,
    SingleEventComponent,
    SingleServiceComponent,
    EditEventComponent,
    EditServiceComponent,
    ServiceListComponent,
    ReservationsComponent,
    ProfileComponent,
    AboutUsComponent
  ],
  imports: [
    MatDatepickerModule,
    MatNativeDateModule,

    MatSelectModule,
    ReactiveFormsModule,
    NgbModule,
    MatFormFieldModule,
    MatRadioModule,
    MatInputModule,
    FormsModule,
    MatCardModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    SlideshowModule,
    MatGridListModule,
    HttpClientModule
  ],
  entryComponents: [LoginComponent],
  providers: [
    EventsService,
    AuthService,
    EventsService,
    AuthGuard,

    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
