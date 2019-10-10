import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
declare var cordova: any;

@Component({
    selector: 'page-home',
    templateUrl: 'home.html'
})
export class HomePage {

    imgSrc: string;

    constructor(public navCtrl: NavController) {

    }
    gotoPage1() {

        // cordova.plugins.lwtchtoast.new_activity();
        cordova.plugins.PluginName.new_activity();
        // this.navCtrl.push('Page1Page');
    }

    scanner() {
        // cordova.plugins.TesseractScanner.tesseract_scanner(null, () => { }, () => { });
        cordova.plugins.TessocrScanner.tessocr_scanner(null, (buffer) => {
            console.log(buffer);
            this.imgSrc = null;
            this.imgSrc = this.convertBufferToBase64(buffer);
        }, () => {

        });
    }

    autofocus() {
        cordova.plugins.CameraAutoFocusCallBack.autoFocus(null, (data) => {
            console.log(data)
        }, () => { });
    }

    convertBufferToBase64(buffer) {
        var mime;
        var a = new Uint8Array(buffer);
        var nb = a.length;
        if (nb < 4)
            return null;
        var b0 = a[0];
        var b1 = a[1];
        var b2 = a[2];
        var b3 = a[3];
        if (b0 == 0x89 && b1 == 0x50 && b2 == 0x4E && b3 == 0x47)
            mime = 'image/png';
        else if (b0 == 0xff && b1 == 0xd8)
            mime = 'image/jpeg';
        else if (b0 == 0x47 && b1 == 0x49 && b2 == 0x46)
            mime = 'image/gif';
        else
            return null;
        var binary = "";
        for (var i = 0; i < nb; i++)
            binary += String.fromCharCode(a[i]);
        var base64 = window.btoa(binary);
        return `data:${mime};base64,${base64}`;
    }
}
