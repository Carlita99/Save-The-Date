import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { pipe } from "rxjs";

@Injectable({ providedIn: "root" })
export class ReservationsService {
  constructor(private http: HttpClient) {}

  getServiceReservations(id) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/reservationcontroller?f=getServiceReservations",
        { service: id }
      )
      .toPromise();
  }
  getEventReservations(id) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/reservationcontroller?f=getEventReservations",
        {
          ide: id
        }
      )
      .toPromise();
  }

  updateReservation(id, ide, date, price, confirmed) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/Controllers/reservationcontroller?f=editservicereservation",
        {
          id: id,
          date: date,
          confirmed: confirmed,
          price: price,
          ide: ide
        }
      )
      .toPromise();
  }
}
