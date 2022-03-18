import { Component, OnInit } from "@angular/core";
import { ServicesService } from "../services/services.service";
import { ReservationsService } from "../services/reservations.service";
import { ActivatedRoute } from "@angular/router";
import { EventsService } from "../services/events.service";
import { NgForm } from "@angular/forms";
import { isNull } from "util";

@Component({
  selector: "app-reservations",
  templateUrl: "./reservations.component.html",
  styleUrls: ["./reservations.component.css"]
})
export class ReservationsComponent implements OnInit {
  constructor(
    private servicesService: ServicesService,
    private eventService: EventsService,
    private resService: ReservationsService,
    private route: ActivatedRoute
  ) {
    this.id = route.snapshot.paramMap.get("id");
    // servicesService.fetchServices().then(() => {
    //   this.service = servicesService.getServices().filter(serv => {
    //     return serv.id == this.id;
    //   })[0];
    //   console.log(this.service);
    // });
    this.resService.getServiceReservations(this.id).then(res => {
      console.log(res);
      this.reservations = res;
    });
    this.eventService
      .fetchEvents()
      .then(() => {
        this.events = this.eventService.getEvents();
        console.log(this.events);
      })
      .then(() => {
        this.reservations.forEach(res => {
          if (res.confirmed == "") {
            const er = {
              event: this.events.filter(ev => {
                return ev.event.ide == res.ide;
              })[0],
              reservation: res
            };
            this.eventRes.push(er);
            console.log(this.eventRes);
          }
        });
        this.loading = false;
      });
  }
  loading = true;
  eventRes = [];
  id;
  service;
  reservations = [];
  events = [];
  ngOnInit() {}
  reject(res) {
    console.log(res);
    const id = res.reservation.id;
    const date = res.reservation.date;
    const price = 0;
    const confirmed = 0;
    const ide = res.reservation.ide;
    this.resService
      .updateReservation(id, ide, date, price, confirmed)
      .then(() => {
        this.eventRes = this.eventRes.filter(er => {
          return er.reservation.id != id;
        });
      });
  }
  onSubmit(form: NgForm) {
    const id = form.value.res;
    const price = form.value.price;
    const ide = form.value.ide;
    const confirmed = 1;
    const date = form.value.date;
    console.log(form);
    this.resService
      .updateReservation(id, ide, date, price, confirmed)
      .then(() => {
        this.eventRes = this.eventRes.filter(er => {
          return er.reservation.id != id;
        });
      });
  }
}
