import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
declare var cordova: any;

@Component({
  selector: 'page-about',
  templateUrl: 'about.html'
})
export class AboutPage {

  constructor(public navCtrl: NavController) {
    cordova.plugins.PluginName.new_activity();
  }

  ionViewDidLoad() {
    cordova.plugins.PluginName.prepare();
  }
}
