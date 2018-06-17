var exec = require('cordova/exec');

exports.show = function (params, success, error) {
    exec(success, error, 'native-date-picker', 'show', [params]);
};
