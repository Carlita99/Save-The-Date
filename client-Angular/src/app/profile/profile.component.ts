import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { EventsService } from "../services/events.service";
import { ServicesService } from "../services/services.service";

@Component({
  selector: "app-profile",
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.css"]
})
export class ProfileComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private eventService: EventsService,
    private serviceService: ServicesService
  ) {}

  ngOnInit() {
    this.us = JSON.parse(localStorage.getItem("user"));
    this.email = this.route.snapshot.paramMap.get("email");
    this.authService.getUser(this.email).then(user => {
      this.user = user;
      const lang = JSON.parse(this.user.user.languages);
      lang.forEach(element => {
        this.languages.push(element);
      });
      console.log(typeof this.languages);
      console.log(this.languages);
      console.log(this.user);
    });

    this.eventService.fetchEvents().then(() => {
      this.events = this.eventService.getEvents().filter(ev => {
        return ev.event.organizer == this.email;
      });

      console.log(this.events);
    });

    this.serviceService.fetchServicesAndProviders().then(() => {
      this.services = this.serviceService
        .getServicesandProviders()
        .filter(serv => {
          return serv.service.provider == this.email;
        });
      console.log(this.services);
    });
  }
  email;
  languages = [];
  events;
  services;
  user;
  us;
  goToServices() {
    this.router.navigate(["/services-list", this.user.user.email]);
  }
  editProfile() {
    this.router.navigate(["/edit-profile"]);
  }
  goToEvents() {
    this.router.navigate(["/events"], {
      queryParams: { user: this.user.user.email }
    });
  }
}
