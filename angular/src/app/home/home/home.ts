import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {
  protected title = 'angular';
}
