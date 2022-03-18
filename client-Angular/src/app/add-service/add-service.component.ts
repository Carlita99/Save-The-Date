import { Component, OnInit, ComponentFactoryResolver } from "@angular/core";
import { ServicesService } from "../services/services.service";
import { NgForm, FormControl } from "@angular/forms";
import { ActivatedRouteSnapshot, ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-add-service",
  templateUrl: "./add-service.component.html",
  styleUrls: ["./add-service.component.css"]
})
export class AddServiceComponent implements OnInit {
  constructor(
    private servicesService: ServicesService,
    private route: ActivatedRoute
  ) {
    // this.serviceTypes = this.route.snapshot.data.items.serviceTypes; get data from resolver
    this.servicesService.fetchServicesTypes().then(() => {
      this.serviceTypes = this.servicesService.getServicesTypes();
      console.log(this.serviceTypes);
    });

    //check if user is a service provider
    this.servicesService.getProviderName().then(res => {
      if (res.provider_Name) {
        this.isProvider = true;
      } else {
        this.isProvider = false;
      }
    });
  }

  isProvider: boolean;

  imgSrc;
  serviceType = new FormControl();
  serviceTypes;
  ngOnInit() {}
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
  onSubmit(form: NgForm) {
    const name = form.value.name;
    const location = form.value.location;
    const opHour = form.value.openingHour;
    const clHour = form.value.closingHour;
    const desc = form.value.desc;
    const type = this.serviceType.value;

    this.servicesService.addService(
      name,
      location,
      opHour,
      clHour,
      desc,
      type,
      this.imgSrc
    );
  }

  providerSubmit(form: NgForm) {
    console.log(form);
    const providerName = form.value.providerName;
    this.servicesService.setProviderName(providerName).then(() => {
      this.isProvider = true;
    });
  }
}
