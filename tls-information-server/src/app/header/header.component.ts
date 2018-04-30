import { Component, OnInit } from '@angular/core';
import { AppComponent }   from '../app.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  href: string = "";
  menuItems = [
    {name:'Home', link:'/'},
    {name:'Features', link:'/features'},
    {name:'Download', link:'/download'},
    {name:'Statistics', link:'/statistics'},
    {name:'About', link:'/about'}
  ];

  constructor(public rootComponent: AppComponent) {}

  ngOnInit() {
    this.href = window.location.pathname;
  }

}
