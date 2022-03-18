import { Component, OnInit } from "@angular/core";
import { ServicesService } from "../services/services.service";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-single-service",
  templateUrl: "./single-service.component.html",
  styleUrls: ["./single-service.component.css"]
})
export class SingleServiceComponent implements OnInit {
  constructor(
    private servicesService: ServicesService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get("id");
    this.service = this.servicesService
      .fetchServicesAndProviders()
      .then(res => {
        this.service = this.servicesService
          .getServicesandProviders()
          .filter(ser => {
            return ser.service.ids == this.id;
          })[0];
        console.log(this.service);
        this.imgSrc = this.service.service.pictures;
      });
    this.user = JSON.parse(localStorage.getItem("user"));
  }
  imgSrc;
  id;
  service;
  user;
}
