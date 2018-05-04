import { Component, OnInit } from '@angular/core';
import { AppComponent }   from '../app.component';

@Component({
  selector: 'app-download',
  templateUrl: './download.component.html',
  styleUrls: ['./download.component.css']
})
export class DownloadComponent implements OnInit {

  constructor(public rootComponent: AppComponent) { }

  ngOnInit() {
  }

}
