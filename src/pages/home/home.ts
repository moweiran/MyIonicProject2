import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
declare var cordova: any;

@Component({
    selector: 'page-home',
    templateUrl: 'home.html'
})
export class HomePage {

    constructor(public navCtrl: NavController) {

    }
    gotoPage1() {

        // cordova.plugins.lwtchtoast.new_activity();
        cordova.plugins.PluginName.new_activity();
        // this.navCtrl.push('Page1Page');
    }

    scanner() {
        cordova.plugins.TesseractScanner.tesseract_scanner(null, () => { }, () => { });
    }
}
