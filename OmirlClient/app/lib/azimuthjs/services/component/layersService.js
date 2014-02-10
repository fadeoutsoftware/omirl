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
                // No: create it
                this.m_oWeatherLayer = new OpenLayers.Layer.Vector("Weather");
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
        /**
         * Creates an Open Layer style for stations Data
         * @returns {OpenLayers.Style}
         */
        getStationsLayerStyle: function() {
            // Define three colors that will be used to style the cluster features
            // depending on the number of features they contain.
            var colors = {
                low: "rgb(181, 226, 140)",
                middle: "rgb(241, 211, 87)",
                high: "rgb(253, 156, 115)"
            };

            // Define three rules to style the cluster features.
            var lowRule = new OpenLayers.Rule({
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.LESS_THAN,
                    property: "value",
                    value: 15.0
                }),
                symbolizer: {
                    fillColor: colors.low,
                    fillOpacity: 0.9,
                    strokeColor: colors.low,
                    strokeOpacity: 0.5,
                    strokeWidth: "${strokeFunction}",
                    pointRadius: "${radiusFunction}",
                    label: "${valueFunction}",
                    labelOutlineWidth: 1,
                    fontColor: "#ffffff",
                    fontOpacity: 0.8,
                    fontSize: "12px"
                }
            });
            var middleRule = new OpenLayers.Rule({
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.BETWEEN,
                    property: "value",
                    lowerBoundary: 15.0,
                    upperBoundary: 50.0
                }),
                symbolizer: {
                    fillColor: colors.middle,
                    fillOpacity: 0.9,
                    strokeColor: colors.middle,
                    strokeOpacity: 0.5,
                    strokeWidth: "${strokeFunction}",
                    pointRadius: "${radiusFunction}",
                    label: "${valueFunction}",
                    labelOutlineWidth: 1,
                    fontColor: "#ffffff",
                    fontOpacity: 0.8,
                    fontSize: "12px"
                }
            });
            var highRule = new OpenLayers.Rule({
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.GREATER_THAN,
                    property: "value",
                    value: 50.0
                }),
                symbolizer: {
                    fillColor: colors.high,
                    fillOpacity: 0.9,
                    strokeColor: colors.high,
                    strokeOpacity: 0.5,
                    strokeWidth: "${strokeFunction}",
                    pointRadius: "${radiusFunction}",
                    label: "${valueFunction}",
                    labelOutlineWidth: 1,
                    fontColor: "#ffffff",
                    fontOpacity: 0.8,
                    fontSize: "12px"
                }
            });

            // Create a Style that uses the three previous rules
            var style = new OpenLayers.Style(null, {
                rules: [lowRule, middleRule, highRule],
                context: {
                    valueFunction: function(feature) {
                        if (feature.layer.map.zoom < 11) return "";
                        else return feature.attributes.value;
                    },
                    radiusFunction: function(feature) {
                        if (feature.layer.map.zoom < 11)  return 5;
                        else return 15;
                    },
                    strokeFunction: function(feature) {
                        if (feature.layer.map.zoom < 11)  return 3;
                        else return 12;
                    }
                }
            });

            return style;
        }

};
    return oLayerService;
  });

})()
