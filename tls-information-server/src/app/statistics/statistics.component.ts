import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  totalTestCount = 0;
  totalInterceptionCount = 0;

  constructor() { }

  ngOnInit() {
    //
  }

}
