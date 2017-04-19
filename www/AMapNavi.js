var exec = require('cordova/exec');

exports.navigation = function (startPoint, endPoint, NavType, successCallback, errorCallback) {
    successCallback = successCallback || function () { };
    errorCallback = errorCallback || function () { };
    var isNumber = function (point) {
        return typeof point.lng === 'number' && typeof point.lat === 'number';
    };
    if (!isNumber(startPoint)) {
        errorCallback('start point invalid');

    } else if (!isNumber(endPoint)) {
        errorCallback('end point invaild');
    } else {
        exec(successCallback, errorCallback, "AMapNavigation", 'navigation', [startPoint.lng, startPoint.lat, endPoint.lng, endPoint.lat, NavType.toString()]);
    }
};
