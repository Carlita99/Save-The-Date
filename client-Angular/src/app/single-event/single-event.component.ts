import { Component, OnInit } from "@angular/core";
import { EventsService } from "../services/events.service";
import { ActivatedRoute } from "@angular/router";
import { ServicesService } from "../services/services.service";
import { ReservationsService } from "../services/reservations.service";

@Component({
  selector: "app-single-event",
  templateUrl: "./single-event.component.html",
  styleUrls: ["./single-event.component.css"]
})
export class SingleEventComponent implements OnInit {
  constructor(
    private eventsService: EventsService,
    private route: ActivatedRoute,
    private servicesService: ServicesService,
    private reservationService: ReservationsService
  ) {}
  user = JSON.parse(localStorage.getItem("user"));
  ngOnInit() {
    this.event = this.eventsService.getEventById(
      this.route.snapshot.paramMap.get("id")
    );

    this.reservationService
      .getEventReservations(this.event.event.ide)
      .then(res => {
        return (this.reservations = res);
      })
      .then(() => {
        return this.servicesService.fetchServices();
      })
      .then(() => {
        this.services = this.servicesService.getServices();
        this.reservedServices = this.servicesService.getServices();
        this.reservedServices = this.reservedServices.filter(serv => {
          return this.reservations.some(res => {
            return serv.ids == res.ids;
          });
        });
        console.log(this.event);
        console.log(this.reservedServices);
      });
  }
  services;
  reservedServices;
  reservations;
  event;
}
