var exec = require('cordova/exec');

exports.tesseract_scanner = function (arg0, success, error) {
    exec(success, error, 'TesseractScanner', 'tesseract_scanner', [arg0]);
};
