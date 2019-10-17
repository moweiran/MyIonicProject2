import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { AboutPage } from '../about/about';
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

        this.navCtrl.push(AboutPage);
    }

    scanner() {
        cordova.plugins.TesseractScanner.tesseract_scanner(null, () => { }, () => { });
    }
}
