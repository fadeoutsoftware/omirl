/**
 * @license AzimuthJS
 * (c) 2012-2013 Matt Priour
 * License: MIT
 */
(function() {

    var mod = angular.module('az.directives');

    mod.directive('olMap', ['az.config', 'az.services.layersService', 'az.services.mapService', '$parse',
        function(config, layerService, mapService, $parse) {
        var defaults = config.defaults;
        return {
            restrict: 'EA',
            priority: -10,
            link: function(scope, elem, attrs) {

                var aoLayers = layerService.getBaseLayers();
                var oCenter = attrs.center ? attrs.center.split(',') : defaults.CENTER.split(',').reverse();
                var iZoom = attrs.zoom || defaults.ZOOM;
                var oProjection = attrs.projection || attrs.proj || attrs.crs || 'EPSG:' + defaults.CRS;
                var dispProj = attrs.dispProjection || attrs.dispProj || 'EPSG:' + defaults.DISP_CRS;
                var asControls = (attrs.controls || defaults.OL_CONTROLS).split(',');
                var aoControlOptions = angular.extend(defaults.OL_CTRL_OPTS, $parse(attrs.controlOpts)());
                var aoMapControls = [];

                $.each(asControls, function(iIndex, sControl) {
                    var opts = aoControlOptions[sControl] || undefined;
                    sControl = sControl.replace(/^\w/, function(m) {
                        return m.toUpperCase()
                    });
                    aoMapControls.push(new OpenLayers.Control[sControl](opts));
                });

                var listeners = {'zoomstart': layerService.onZoomStart, 'zoomend': layerService.onZoomEnd};

                $.each(attrs, function(key, val) {
                    var evtType = key.match(/map([A-Z]\w+)/);
                    if(evtType) {
                        evtType = evtType[1].replace(/^[A-Z]/,function(m){return m.toLowerCase()});
                        var $event = {
                            type: key
                        };
                        listeners[evtType] = function(evtObj) {
                            evtObj.evtType = evtObj.type;
                            delete evtObj.type;
                            elem.trigger(angular.extend({}, $event, evtObj));
                            //We create an $apply if it isn't happening.
                            //copied from angular-ui uiMap class
                            if(!scope.$$phase) scope.$apply();
                        };
                        var fn = $parse(val);
                        elem.bind(key, function (evt) {
                          fn(scope, evt);
						});
                    }
                });

                oCenter = new OpenLayers.LonLat(oCenter).transform('EPSG:4326', oProjection);

                var map = new OpenLayers.Map(elem[0], {
                    'projection': oProjection,
                    'displayProjection': dispProj,
                    'controls': aoMapControls,
                    'center': oCenter,
                    'zoom': iZoom,
                    'layers': aoLayers,
                    'eventListeners': listeners,
                    'transitionEffect': null,
                    'zoomMethod': null,
                    'zoomDuration': 10
                });

                //var model = $parse(attrs.$attr.olMap);
                //var model = $parse(attrs.olMap);
                //Set scope variable for the map
                //if(model){model.assign(scope, map);}
                mapService.map = map;

                if (mapService.readyCallback != null) {
                    mapService.readyCallback(mapService.callbackArg);
                }
            }
        };
    }]);
})()
