import {
  Component,
  OnInit,
  ViewChild,
  HostListener,
  AfterViewChecked
} from "@angular/core";
import { EventsService } from "../services/events.service";
import { Event } from "../models/Event";
import { ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-home-events",
  templateUrl: "./home-events.component.html",
  styleUrls: ["./home-events.component.css"]
})
export class HomeEventsComponent implements OnInit, AfterViewChecked {
  ngAfterViewChecked(): void {
    console.log("Hello");
    this.setCols();
  }
  colnum: number = 4;
  @ViewChild("container") container;
  constructor(
    private eventsService: EventsService,
    private route: ActivatedRoute
  ) {}
  events = [];
  loading = true;
  ngOnInit() {
    const email = this.route.snapshot.queryParams.user;
    this.eventsService.fetchEvents();
    this.eventsService.eventsChanged.subscribe(events => {
      this.events = events;
      this.loading = false;
      console.log(email);
      if (email) {
        this.events = this.events.filter(ev => {
          return ev.organizer.email == email;
        });
      }

      console.log(this.events);
    });
  }
  setCols() {
    let width = window.innerWidth;
    this.colnum = Math.round(width / 440);
    console.log(this.colnum);
  }

  @HostListener("window:resize", ["$event"])
  onResize(event) {
    this.setCols();
  }
}
