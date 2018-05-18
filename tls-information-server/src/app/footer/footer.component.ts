import { Component, OnInit } from '@angular/core';
import { AppComponent }   from '../app.component';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {

  team = [
    {name:'Johannes Schleger', link:'mailto:johannes.schleger@tum.de'},
    {name:'Jonas Jelten', link:'https://net.in.tum.de/members/jelten/'},
    {name:'Florian Wohlfart', link:'https://net.in.tum.de/members/wohlfart/'},
    {name:'Quirin Scheitle', link:'https://net.in.tum.de/members/scheitle/'},
    {name:'Prof. Dr.-Ing. Georg Carle', link:'https://net.in.tum.de/members/carle/'},
  ];
  links = [
    {name:'FAQ', link:'faq'},
    {name:'Privacy', link:'privacy'},
    {name:'Contact', link:'about'},
  ]

  constructor(public rootComponent: AppComponent) { }

  ngOnInit() {
  }

}
