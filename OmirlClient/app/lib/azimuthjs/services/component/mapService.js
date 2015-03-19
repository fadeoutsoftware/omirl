/**
 * @license AzimuthJS
 * (c) 2012-2013 Matt Priour
 * License: MIT
 */
(function() {
    angular.module('az.services').
    value('az.services.mapService', {
        map: null,
        stationsPopupControllerAdded: false,
        sectionsPopupControllerAdded: false,
        readyCallback: null,
        callbackArg: null
    })
}) ()