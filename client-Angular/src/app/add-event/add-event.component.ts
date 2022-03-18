import { Component, OnInit } from "@angular/core";
import { EditServiceComponent } from "../edit-service/edit-service.component";
import { EventsService } from "../services/events.service";
import { ServicesService } from "../services/services.service";
import { FormControl, NgForm } from "@angular/forms";

@Component({
  selector: "app-add-event",
  templateUrl: "./add-event.component.html",
  styleUrls: ["./add-event.component.css"]
})
export class AddEventComponent implements OnInit {
  imgSrc;
  show = false;
  reservations = [];
  constructor(
    private eventService: EventsService,
    private servicesService: ServicesService
  ) {
    this.eventService.fetchEventTypes().then(() => {
      this.eventTypes = this.eventService.getEventTypes();
      console.log(this.eventTypes);
    });
    this.servicesService.fetchServices().then(() => {
      this.services = this.servicesService.getServices();
      console.log(this.services);
    });
    this.servicesService.fetchServices().then(() => {
      this.services = this.servicesService.getServices();
      console.log(this.services);
    });

    this.eventService.getOrganizerName().then(res => {
      console.log(res);
      if (res.organizer_name) {
        this.isOrganizer = true;
      } else {
        this.isOrganizer = false;
      }
    });
  }
  isOrganizer: boolean;
  service = new FormControl();
  event = new FormControl();
  eventTypes;
  services;
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
    const reserv = this.reservations.filter(res => {
      console.log(res);
      return res.ids != id.value;
    });
    this.reservations = reserv;
  }
  add() {
    this.show = true;
  }
  selected(event) {
    const id = this.service.value;
    console.log(id);
    const serv = this.services.filter(service => {
      console.log(service);
      return service.ids == id;
    })[0];
    this.reservations.push(serv);
    this.show = false;
    this.service.setValue(null);
    console.log(this.reservations);
  }
  onSubmit(form: NgForm) {
    const name = form.value.name;
    const type = this.event.value;
    console.log(type);
    const nbGuests = form.value.nbGuests;
    const date =
      form.value.date.getUTCFullYear() +
      "-" +
      (1 + form.value.date.getMonth()) +
      "-" +
      form.value.date.getDate();
    console.log(date);
    console.log(date);
    const start = form.value.start;
    const duration = form.value.duration;
    const description = form.value.desc;
    const highlights = form.value.highlights;
    this.eventService.createEvent(
      name,
      type,
      nbGuests,
      date,
      start,
      duration,
      description,
      highlights,
      this.imgSrc,
      this.reservations
    );
  }

  organizerSubmit(form: NgForm) {
    console.log(form);
    const organizerName = form.value.organizerName;
    this.eventService.setOrganizerName(organizerName).then(res => {
      console.log(res);
      this.isOrganizer = true;
    });
  }

  ngOnInit() {}
}
