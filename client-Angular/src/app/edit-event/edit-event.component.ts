import { Component, OnInit, ComponentFactoryResolver } from "@angular/core";
import { EventsService } from "../services/events.service";
import { ActivatedRoute, Router } from "@angular/router";
import { ReservationsService } from "../services/reservations.service";
import { ServicesService } from "../services/services.service";
import { FormControl, NgForm } from "@angular/forms";

@Component({
  selector: "app-edit-event",
  templateUrl: "./edit-event.component.html",
  styleUrls: ["./edit-event.component.css"]
})
export class EditEventComponent implements OnInit {
  constructor(
    private router: Router,
    private eventService: EventsService,
    private route: ActivatedRoute,
    private reservationService: ReservationsService,
    private servicesService: ServicesService
  ) {}
  event;
  show = false;
  eventTypes;
  id;
  date;
  services;
  imgSrc;
  service = new FormControl();
  eventControl = new FormControl();

  newReservations = [];
  deletedReservations = [];
  reservations;
  reservedServices = [];
  add() {
    this.show = true;
  }
  picChanged(event) {
    if (event.target.files.length === 0) return;

    var mimeType = event.target.files[0].type;
    if (mimeType.match(/image\/*/) == null) {
      console.log("Only images are supported.");
      return;
    }
    var reader = new FileReader();
    reader.readAsDataURL(event.target.files[0]);
    reader.onload = _event => {
      this.imgSrc = reader.result;
    };
  }

  remove(id) {
    id = id.value;
    this.deletedReservations.push(
      this.reservedServices.filter(serv => {
        return serv.ids == id;
      })[0]
    );

    this.reservedServices = this.reservedServices.filter(serv => {
      return serv.ids != id;
    });
    console.log(this.reservedServices);
    console.log(this.deletedReservations);
  }
  selected(event) {
    this.newReservations.push(
      this.services.filter(serv => {
        return serv.ids === this.service.value;
      })[0]
    );
    this.reservedServices.push(
      this.services.filter(serv => {
        return serv.ids === this.service.value;
      })[0]
    );
    this.show = false;
  }
  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get("id");

    this.eventService.fetchEventTypes().then(() => {
      this.eventTypes = this.eventService.getEventTypes();
    });

    this.eventService.fetchEvents().then(res => {
      this.event = this.eventService.getEventById(this.id);
      console.log(this.event);
      this.date = new Date(this.event.event.date);
      console.log(this.date);
      this.imgSrc = this.event.event.pictures;
    });

    this.reservationService
      .getEventReservations(this.id)
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
      });
  }
  onSubmit(form: NgForm) {
    const name = form.value.eventName;
    console.log(name);
    const type = this.eventControl.value;
    console.log(type);
    const nbGuests = form.value.nbGuests;
    const evdate =
      form.value.date.getUTCFullYear() +
      "-" +
      (1 + form.value.date.getMonth()) +
      "-" +
      form.value.date.getDate();
    console.log(evdate);
    const start = form.value.start;
    const duration = form.value.duration;
    const description = form.value.desc;
    const highlights = form.value.highlights;
    this.eventService
      .editEvent(
        this.id,
        name,
        type,
        nbGuests,
        evdate,
        start,
        duration,
        description,
        highlights,
        this.imgSrc,
        this.deletedReservations,
        this.newReservations
      )
      .then(() => {
        this.router.navigate(["/events"], {
          queryParams: { email: JSON.parse(localStorage.getItem("user")).email }
        });
      });
  }
}
