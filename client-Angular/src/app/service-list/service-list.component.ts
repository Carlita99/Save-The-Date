import { Component, OnInit, HostListener, ViewChild } from "@angular/core";
import { ServicesService } from "../services/services.service";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-service-list",
  templateUrl: "./service-list.component.html",
  styleUrls: ["./service-list.component.css"]
})
export class ServiceListComponent implements OnInit {
  constructor(
    private servicesService: ServicesService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.email = this.route.snapshot.paramMap.get("email");
    this.servicesService.fetchServicesAndProviders().then(res => {
      this.services = this.servicesService
        .getServicesandProviders()
        .filter(serv => {
          console.log(serv);
          console.log(this.email);
          return serv.service.provider == this.email;
        });
      console.log(this.services);
      this.loading = false;
    });
  }
  loading = true;
  @ViewChild("container") container;
  email;
  colnum = 4;
  setCols() {
    let width = window.innerWidth;

    this.colnum = Math.round(width / 450);
  }

  @HostListener("window:resize", ["$event"])
  onResize(event) {
    this.setCols();
  }
  services;
}
