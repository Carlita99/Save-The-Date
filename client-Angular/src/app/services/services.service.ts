import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";

@Injectable({ providedIn: "root" })
export class ServicesService {
  constructor(private http: HttpClient, private route: Router) {}
  services;
  servTypes;
  servicesAndProviders;

  fetchServicesAndProviders() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/servicecontroller?f=getProvidersandServices"
      )
      .toPromise()
      .then(res => {
        this.servicesAndProviders = res.services;
        console.log(this.servicesAndProviders);
      });
  }

  getServicesandProviders() {
    return this.servicesAndProviders;
  }
  fetchServicesTypes() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/servicecontroller?f=getServiceTypes"
      )
      .toPromise()
      .then(res => {
        this.servTypes = res.serviceTypes;
      });
  }
  getServicesTypes() {
    return this.servTypes;
  }
  editService(id, name, location, op, cl, desc, img) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/util/file_upload?f=saveFile",
        {
          base64string: img
        }
      )
      .toPromise()
      .then(pic => {
        return this.http
          .post<any>(
            "http://localhost:80/save_the_date/Controllers/servicecontroller?f=editService",
            {
              id: id,
              name: name,
              location: location,
              openinghour: op,
              closinghour: cl,
              pictures: img,
              description: desc
            }
          )
          .toPromise();
      })

      .then(() => {
        return this.fetchServices();
      })
      .then(() => {
        this.route.navigate([
          "services-list",
          JSON.parse(localStorage.getItem("user")).email
        ]);
      });
  }

  fetchServices(): any {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/servicecontroller?f=getServices"
      )
      .toPromise()
      .then(res => {
        console.log(res);
        this.services = res.services;
        console.log(this.services);
      });
  }
  getServices() {
    return this.services;
  }
  addService(name, location, opHour, clHour, desc, type, imgSrc) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/util/file_upload?f=saveFile",
        {
          base64string: imgSrc
        }
      )
      .toPromise()
      .then(pic => {
        console.log(pic);
        return this.http
          .post<any>(
            "http://localhost:80/save_the_date/Controllers/servicecontroller?f=addService",
            {
              name: name,
              description: desc,
              location: location,
              openinghour: opHour,
              closinghour: clHour,
              typeservice: type,
              pictures: pic
            }
          )
          .toPromise();
      })

      .then(() => {
        this.route.navigate([
          "services-list",
          JSON.parse(localStorage.getItem("user")).email
        ]);
      });
  }
  setProviderName(name) {
    return this.http
      .post(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=setProviderName",
        {
          provider_name: name
        }
      )
      .toPromise();
  }

  getProviderName() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=getProviderName"
      )
      .toPromise();
  }
}
