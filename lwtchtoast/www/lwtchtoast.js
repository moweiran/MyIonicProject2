var exec = require('cordova/exec');

exports.new_activity = function () {
    exec(function (res) { }, function (err) { }, "LwtchToast", "new_activity", []);
}