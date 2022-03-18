import { Time } from "@angular/common";

export class Event {
  idEvent: number;
  name: string;
  numberOfGuests: number;
  date: Date;
  starting_hour: number;
  duration: number;
  description: string;
  highlights: string;
  totalCost: number;
  organizer: string;
  typeEvent: string;
  pictures: string[];
  constructor(
    idEvent: number,
    name: string,
    numberOfGuests: number,
    date: Date,
    starting_hour: number,
    duration: number,
    description: string,
    highlights: string,
    totalCost: number,
    organizer: string,
    typeEvent: string,
    pictures: string[]
  ) {
    this.idEvent = idEvent;
    this.name = name;
    this.numberOfGuests - numberOfGuests;
    this.date = date;
    this.starting_hour = starting_hour;
    this.duration = duration;
    this.description = description;
    this.organizer = organizer;
    this.highlights = highlights;
    this.totalCost = totalCost;
    this.typeEvent = typeEvent;
    this.pictures = pictures;
  }
}
