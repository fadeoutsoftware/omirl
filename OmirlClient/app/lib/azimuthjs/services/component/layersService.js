/**
 * @license AzimuthJS
 * (c) 2012-2013 Matt Priour
 * License: MIT
 *
 * Extended and revisited by
 * (c) 2014 Fadeout Software srl
 */
(function() {
angular.module('az.services').factory('az.services.layersService',function($rootScope) {
    var oLayerService = {
    	m_aoBaseLayers: [],
        m_oDynamicLayer: null,
        m_oSensorsLayer: null,
        m_oWeatherLayer: null,
        m_oMarkerLayer: null,
        m_aoStaticLayers: [],
        m_aoSensorLayerColorRanges: [
            {"lmt":0.2,"clr":"#FFFFFF"},
            {"lmt":1,"clr":"#E6F5FF"},
            {"lmt":2,"clr":"#E1F0FF"},
            {"lmt":3,"clr":"#DCEBFF"},
            {"lmt":5,"clr":"#D5E6FF"},
            {"lmt":7,"clr":"#D0E0FF"},
            {"lmt":10,"clr":"#D0E0FF"},
            {"lmt":15,"clr":"#8FB0FF"},
            {"lmt":20,"clr":"#8FB0FF"},
            {"lmt":25,"clr":"#8FB0FF"},
            {"lmt":30,"clr":"#009F1F"},
            {"lmt":40,"clr":"#3FBF3F"},
            {"lmt":50,"clr":"#B0D06F"},
            {"lmt":60,"clr":"#BFF86F"},
            {"lmt":70,"clr":"#FFFFA0"},
            {"lmt":80,"clr":"#FFFF78"},
            {"lmt":90,"clr":"#FFF810"},
            {"lmt":100,"clr":"#FFA00F"},
            {"lmt":110,"clr":"#FF0000"},
            {"lmt":130,"clr":"#E00000"},
            {"lmt":150,"clr":"#BF0000"},
            {"lmt":170,"clr":"#AA0000"},
            {"lmt":200,"clr":"#960000"},
            {"lmt":220,"clr":"#870000"},
            {"lmt":250,"clr":"#870000"},
            {"lmt":300,"clr":"#6E0000"},
            {"lmt":325,"clr":"#640069"},
            {"lmt":350,"clr":"#6E0073"},
            {"lmt":375,"clr":"#78007D"},
            {"lmt":400,"clr":"#8C0091"},
            {"lmt":425,"clr":"#96009B"},
            {"lmt":450,"clr":"#B400B9"},
            {"lmt":10000,"clr":"#C800CD"}
        ],
        m_aoHydroSensorLayerColorRanges: [
            {"lmt":1,"clr":"#FFFFFF"},
            {"lmt":2,"clr":"#33CC33"},
            {"lmt":3,"clr":"#FF6600"},
            {"lmt":4,"clr":"#FF0000"},
            {"lmt":5,"clr":"#0033CC"},
            {"lmt":10000,"clr":"#000000"}
        ],
        m_aoSflocLayerColorRanges: [
            {"lmt":10,"clr":"#FFFFFF"},
            {"lmt":20,"clr":"#E6F5FF"},
            {"lmt":30,"clr":"#E1F0FF"},
            {"lmt":40,"clr":"#DCEBFF"},
            {"lmt":50,"clr":"#D5E6FF"},
            {"lmt":60,"clr":"#D0E0FF"},
            {"lmt":70,"clr":"#D0E0FF"},
            {"lmt":80,"clr":"#8FB0FF"},
            {"lmt":90,"clr":"#8FB0FF"},
            {"lmt":100,"clr":"#8FB0FF"},
            {"lmt":110,"clr":"#009F1F"},
            {"lmt":120,"clr":"#3FBF3F"},
            {"lmt":130,"clr":"#B0D06F"},
            {"lmt":140,"clr":"#BFF86F"},
            {"lmt":150,"clr":"#FFFFA0"},
            {"lmt":160,"clr":"#FFFF78"},
            {"lmt":170,"clr":"#FFF810"},
            {"lmt":180,"clr":"#FFA00F"},
            {"lmt":190,"clr":"#FF0000"},
            {"lmt":200,"clr":"#E00000"},
            {"lmt":210,"clr":"#BF0000"},
            {"lmt":220,"clr":"#AA0000"},
            {"lmt":230,"clr":"#960000"},
            {"lmt":240,"clr":"#870000"},
            {"lmt":250,"clr":"#870000"},
            {"lmt":260,"clr":"#6E0000"},
            {"lmt":270,"clr":"#640069"},
            {"lmt":280,"clr":"#6E0073"},
            {"lmt":290,"clr":"#78007D"},
            {"lmt":300,"clr":"#8C0091"},
            {"lmt":310,"clr":"#96009B"},
            {"lmt":320,"clr":"#B400B9"},
            {"lmt":1000,"clr":"#C800CD"}
        ],
        clarAll: function() {
            this.clearBaseLayers();
            this.clearStaticLayers();
            this.m_oDynamicLayer = null;
            this.m_oMarkerLayer = null;
            this.m_oSensorsLayer = null;
            this.m_oWeatherLayer = null;
        },

        /**
         * Gets Map base layers array
         * @returns {*}
         */
    	getBaseLayers: function(){
    		return this.m_aoBaseLayers;
    	},
        /**
         * Clears Map base layers array
         */
        clearBaseLayers: function () {
            this.m_aoBaseLayers = [];
        },
        /**
         * Adds a Base Layer to the base layers array
         * @param oLayer
         */
        addBaseLayer: function(oLayer) {
            this.m_aoBaseLayers.push(oLayer);
        },
        /**
         * Gets Map Static layers array
         * @returns {*}
         */
        getStaticLayers: function(){
            return this.m_aoStaticLayers;
        },
        /**
         * Clears Map Static layers array
         */
        clearStaticLayers: function () {
            this.m_aoStaticLayers = [];
        },
        /**
         * Adds a Static Layer to the Static layers array
         * @param oLayer
         */
        addStaticLayer: function(oLayer) {
            this.m_aoStaticLayers.push(oLayer);
        },
        removeStaticLayer: function(oLayer) {
            this.m_aoStaticLayers.remove(oLayer);
        },
        /**
         * Gets the actual Dynamic Layer
         * @returns {null}
         */
        getDynamicLayer: function () {
            return this.m_oDynamicLayer;
        },
        /**
         * Sets the actual Dynamic Layer
         * @param oLayer
         */
        setDynamicLayer: function(oLayer) {
            this.m_oDynamicLayer = oLayer;
        },
        /**
         * Gets the sensors layer
         * @returns {null}
         */
        getSensorsLayer: function() {

            if (this.m_oSensorsLayer == null) {
                // No: create it
                var oStyleMap = new OpenLayers.StyleMap(this.getStationsLayerStyle());
                this.m_oSensorsLayer = new OpenLayers.Layer.Vector("Stazioni", {
                    styleMap: oStyleMap,
                    rendererOptions: {zIndexing: true}
                });
            }

            return this.m_oSensorsLayer;
        },
        /**
         * Sets the sensors layer
         * @param oLayer
         */
        setSensorsLayer: function(oLayer) {
            this.m_oSensorsLayer = oLayer;
        },
        /**
         * Gets the Weather Layer
         * @returns {null}
         */
        getWeatherLayer: function() {
            if (this.m_oWeatherLayer == null) {

                var myClusterStyleMap = new OpenLayers.StyleMap({
                    'default':  new OpenLayers.Style({
                        externalGraphic: '${count}',
                        graphicWith: 48,
                        graphicHeight: 48,
                        label: '',
                        labelOutlineColor: "#aaaaaa",
                        labelOutlineWidth: 2
                    }, {
                        context: {
                            count: function(feature) {
                                if (feature.cluster) { // is `.cluster` the array of clustered features
                                    var iFeatures;
                                    var iMean = 0;
                                    for ( iFeatures =0; iFeatures<feature.cluster.length; iFeatures++) {
                                        var oFeature = feature.cluster[iFeatures];
                                        iMean += oFeature.attributes['value'];
                                    }

                                    if (feature.cluster.length>0) {
                                        iMean = iMean/feature.cluster.length;
                                    }

                                    if (iMean<=0) iMean = 0;
                                    if (iMean>32) iMean = 32;

                                    iMean = Math.round(iMean);

                                    return 'img/weather/w'+iMean+'.png';
                                } else { // is not clustered
                                    return 'img/weather/w8.png'; // no label
                                }
                            }
                        }
                    })
                });

                // No: create it
                this.m_oWeatherLayer = new OpenLayers.Layer.Vector("Weather", {
                    strategies: [new OpenLayers.Strategy.Cluster({distance: 25})],
                    styleMap: myClusterStyleMap
                });
                this.m_oWeatherLayer.animationEnabled = false;
            }
            return this.m_oWeatherLayer;
        },
        /**
         * Sets the Weather Layer
         * @param oLayer
         */
        setWeatherLayer: function(oLayer) {
            this.m_oWeatherLayer = oLayer;
        },
        /**
         * Gets the Marker Layer
         * @returns {null}
         */
        getMarkerLayer: function() {
            if (this.m_oMarkerLayer == null) {
                // No: create it
                this.m_oMarkerLayer = new OpenLayers.Layer.Markers("Search Results");
            }
            return this.m_oMarkerLayer;
        },
        /**
         * Sets the Marker Layer
         * @param oLayer
         */
        setMarkerLayer: function(oLayer) {
            this.m_oMarkerLayer = oLayer;
        },
        getSensorsLayerIndex : function() {
            var iIndex = this.m_aoBaseLayers.length;

            if (this.m_oDynamicLayer != null) iIndex ++;

            iIndex += this.m_aoStaticLayers.length;

            return iIndex;
        },
        getWeatherLayerIndex : function() {
            var iIndex = this.m_aoBaseLayers.length;

            if (this.m_oDynamicLayer != null) iIndex ++;

            iIndex += this.m_aoStaticLayers.length;

            if (this.m_oSensorsLayer != null) iIndex ++;

            return iIndex;
        },
        onZoomStart: function () {
            //console.log("Zoom Start");
        },
        onZoomEnd: function () {
            //console.log("Zoom End");
        },
        getStationsLayerColorMap: function (oSensorType) {

            if (oSensorType!="Idro") return this.m_aoSensorLayerColorRanges;
            if (oSensorType!="Sfloc") return this.m_aoSflocLayerColorRanges;
            return this.m_aoHydroSensorLayerColorRanges;
        },
        /**
         * Creates an Open Layer style for stations Data
         * @returns {OpenLayers.Style}
         */
        getStationsLayerStyle: function() {
            // Define three rules to style the cluster features.
            var lowRule = new OpenLayers.Rule({
                symbolizer: {
                    fillColor: "${colorFunction}",
                    fillOpacity: "${opacityFunction}",
                    strokeColor: "${strokeColorFunction}",
                    strokeOpacity: "${strokeOpacityFunction}",
                    strokeWidth: "${strokeWidthFunction}",
                    pointRadius: "${radiusFunction}",
                    label: "${valueFunction}",
                    'labelOutlineColor' : "${colorFunction}",
                    labelOutlineWidth: 2,
                    fontColor: "#ffffff",
                    fontOpacity: 0.8,
                    fontSize: "12px",
                    graphicName: '${graphicNameFunction}',
                    rotation: "${rotationFunction}",
                    externalGraphic: '${externalGraphicFunction}',
                    graphicWidth: '${externalGraphicSize}',
                    graphicHeight: '${externalGraphicSize}'
                }
            });

            var oService = this;

            // Create a Style that uses the three previous rules
            var style = new OpenLayers.Style(null, {
                rules: [lowRule],
                context: {
                    valueFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return '';

                        if (feature.layer.map.zoom < 12) return "";
                        else return feature.attributes.value;
                    },
                    radiusFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return 0;

                        if (feature.layer.map.zoom < 12)  return 5;
                        else return 15;
                    },
                    strokeWidthFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return 0;

                        if (feature.layer.map.zoom < 12)  return 2;
                        else return 12;
                    },
                    strokeOpacityFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return 1;

                        if (feature.layer.map.zoom < 12)  return 1;
                        else return 0.5;
                    },
                    colorFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return undefined;

                        var dValue = feature.attributes.value;

                        if (feature.attributes.sensorType == 'Idro') {
                            dValue = feature.attributes.otherHtml;
                        }

                        if (feature.attributes.sensorType == 'Sfloc'){
                            var now = new Date().getTime(); //now
                            var refDate = new Date(feature.attributes.referenceDate).getTime();
                            var millisec = now - refDate;
                            dValue = Math.floor(millisec / 60000)  //minutes
                        }

                        if (feature.attributes.opacity==-1.0)
                        {
                            return "#AAAAAA";
                        }

                        var aoColorsMap = oService.getStationsLayerColorMap(feature.attributes.sensorType);

                        var iColors = 0;
                        for (iColors = 0; iColors<aoColorsMap.length; iColors++) {
                            var oColorRange = aoColorsMap[iColors];
                            if (dValue<oColorRange.lmt) return oColorRange.clr;
                        }

                        if (dValue<0.2) return ""
                    },
                    opacityFunction: function(feature) {
                        if (feature.attributes.opacity==-1.0)
                        {
                            return 0.9;
                        }
                        return feature.attributes.opacity;
                    },
                    strokeColorFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return null;

                        if (feature.layer.map.zoom < 12) return "#000000";
                        else  {
                            // SAME AS COLOR FUNCTION
                            // I was unable to recall colorFunction from here!!
                            var dValue = feature.attributes.value;

                            if (feature.attributes.sensorType == 'Sfloc'){
                                var now = new Date().getTime(); //now
                                var refDate = new Date(feature.attributes.referenceDate).getTime();
                                var millisec = now - refDate;
                                dValue = Math.floor(millisec / 60000)  //minutes
                            }

                            var aoColorsMap = oService.getStationsLayerColorMap(feature.attributes.sensorType);

                            var iColors = 0;
                            for (iColors = 0; iColors<aoColorsMap.length; iColors++) {
                                var oColorRange = aoColorsMap[iColors];
                                if (dValue<oColorRange.lmt) return oColorRange.clr;
                            }

                            if (dValue<0.2) return ""
                        }
                    },
                    graphicNameFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') return null;

                        if (feature.attributes.sensorType == 'Idro') {
                            if (feature.attributes.increment == 0) {
                                return 'circle';
                                //return 'triangle';
                            }

                            return 'triangle';
                        }
                        else {
                            return 'circle';
                        }
                    },
                    rotationFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Idro') {
                            if (feature.attributes.increment == -1) {
                                return 180;
                            }

                            return 0;
                        }
                        else if (feature.attributes.sensorType == 'Vento') {
                            return feature.attributes.increment;
                        }
                        else {
                            return 0;
                        }

                    },
                    externalGraphicFunction: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') {
                            return feature.attributes.imgPath;
                        }
                        else {
                            return '';
                        }
                    },
                    externalGraphicSize: function(feature) {
                        if (feature.attributes.sensorType == 'Vento') {
                            return 32;
                        }
                        else {
                            return 0;
                        }
                    }
                }
            });

            return style;
        }

};
    return oLayerService;
  });

})()
