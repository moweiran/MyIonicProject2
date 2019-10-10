var exec = require('cordova/exec');

exports.autoFocus = function (arg0, success, error) {
    exec(success, error, 'CameraAutoFocusCallBack', 'autoFocus', [arg0]);
};
