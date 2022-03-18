import { Component, OnInit, Input } from "@angular/core";

@Component({
  selector: "app-event-card",
  templateUrl: "./event-card.component.html",
  styleUrls: ["./event-card.component.css"]
})
export class EventCardComponent implements OnInit {
  constructor() {}
  @Input() name: string;
  @Input() organizer: string;
  @Input() image: string;
  @Input() description: string;
  @Input() profilePic: string;
  @Input() titleLink: string;
  @Input() secondaryLink: string;
  ngOnInit() {}
}
