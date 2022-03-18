import { Event } from "../models/Event";
import { OnInit, Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
@Injectable({ providedIn: "root" })
export class EventsService implements OnInit {
  private events = [];
  eventTypes;
  public eventsChanged: Subject<Event[]> = new Subject<Event[]>();
  ngOnInit(): void {}

  constructor(private http: HttpClient, private route: Router) {}

  fetchEvents() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/eventcontroller?f=getOrganizersandEvents"
      )
      .toPromise()
      .then(
        res => {
          this.events = res.events;
          this.eventsChanged.next(this.events);
        },
        error => {
          console.log(error);
        }
      );
    // return this.http
    //   .get(
    //     "http://localhost:80/save_the_date/Controllers/eventcontroller?f=getEvents"
    //   )
    //   .toPromise()
    //   .then(res => console.log(res));
  }

  fetchEventTypes() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/eventcontroller?f=getEventTypes"
      )
      .toPromise()
      .then(res => {
        this.eventTypes = res.eventTypes;
        console.log(this.eventTypes);
      });
  }

  getEventTypes(): any {
    return this.eventTypes;
  }
  createEvent(
    name,
    type,
    nbGuests,
    date,
    start,
    duration,
    description,
    highlights,
    imgSrc,
    reservations
  ) {
    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/util/file_upload?f=saveFile",
        {
          base64string: imgSrc
        }
      )
      .toPromise()
      .then(img => {
        return this.http
          .post<any>(
            "http://localhost:80/save_the_date/Controllers/eventcontroller?f=addEvent",
            {
              event: {
                nom: name,
                numberOfGuests: nbGuests,
                date: date,
                startingHour: start,
                duration: duration,
                description: description,
                highlights: highlights,
                cost: 0,
                typeEvent: type,
                pictures: img
              }
            }
          )
          .toPromise();
      })

      .then(async res => {
        const event = res.event;
        await reservations.forEach(res => {
          return this.http.post<any>(
            "http://localhost:80/save_the_date/Controllers/reservationcontroller?f=addServiceReservation",
            {
              ide: event.ide,
              ids: res.ids,
              price: 0
            }
          );
        });
      })
      .then(() => {
        this.route.navigate(["events"]);
      });
  }
  getEventById(id) {
    return this.events.filter(event => {
      return event.event.ide == id;
    })[0];
  }
  getEvents() {
    return this.events.slice();
  }

  editEvent(
    id,
    name,
    type,
    nbGuests,
    date,
    start,
    duration,
    description,
    highlights,
    imgSrc,
    deletedReservations,
    newReservations
  ) {
    deletedReservations.forEach(res => {
      this.http
        .post(
          "http://localhost:80/save_the_date/Controllers/reservationController?f=deleteReservationByids",
          {
            ide: id,
            ids: res.ids
          }
        )
        .toPromise()
        .then(res => {
          console.log(res);
        });
    });
    newReservations.forEach(res => {
      this.http
        .post(
          "http://localhost:80/save_the_date/Controllers/reservationController?f=addServiceReservation",
          {
            ide: id,
            ids: res.ids,
            price: 0
          }
        )
        .toPromise()
        .then(res => {
          console.log(res);
        });
    });

    return this.http
      .post<any>(
        "http://localhost:80/save_the_date/util/file_upload?f=saveFile",
        {
          base64string: imgSrc
        }
      )
      .toPromise()
      .then(pic => {
        return this.http
          .post(
            "http://localhost:80/save_the_date/Controllers/eventcontroller?f=editEvent",
            {
              event: {
                id: id,
                name: name,
                numberOfGuests: nbGuests,
                date: date,
                startingHour: start,
                duration: duration,
                description: description,
                highlights: highlights,
                cost: 0,
                typeEvent: type,
                pictures: pic
              }
            }
          )
          .toPromise();
      });
  }

  setOrganizerName(name) {
    console.log(name);
    return this.http
      .post(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=setOrganizerName",
        {
          organizer_name: name
        }
      )
      .toPromise();
  }

  getOrganizerName() {
    return this.http
      .get<any>(
        "http://localhost:80/save_the_date/Controllers/usercontroller?f=getEventOrganizerName"
      )
      .toPromise();
  }
}
