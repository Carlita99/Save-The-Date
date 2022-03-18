import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { ServicesService } from "../services/services.service";
import { NgForm } from "@angular/forms";

@Component({
  selector: "app-edit-service",
  templateUrl: "./edit-service.component.html",
  styleUrls: ["./edit-service.component.css"]
})
export class EditServiceComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private servicesService: ServicesService
  ) {}
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
  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get("id");
    console.log(this.id);
    this.service = this.servicesService.fetchServices().then(res => {
      this.service = this.servicesService.getServices().filter(ser => {
        return ser.ids == this.id;
      })[0];
      console.log(this.service);
      this.imgSrc = this.service.pictures;
      console.log(this.imgSrc);
    });
  }
  id;
  imgSrc;
  service;
  onSubmit(form: NgForm) {
    const name = form.value.name;
    const location = form.value.location;
    const opHour = form.value.openingHour;
    const clHour = form.value.closingHour;
    const description = form.value.desc;
    console.log(form);
    this.servicesService.editService(
      this.id,
      name,
      location,
      opHour,
      clHour,
      description,
      this.imgSrc
    );
  }
}
