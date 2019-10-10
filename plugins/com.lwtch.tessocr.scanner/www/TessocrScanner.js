var exec = require('cordova/exec');

exports.tessocr_scanner = function (arg0, success, error) {
    exec(success, error, 'TessocrScanner', 'tessocr_scanner', [arg0]);
};
