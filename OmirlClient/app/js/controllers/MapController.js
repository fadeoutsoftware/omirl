/**
 * Created by p.campanella on 20/01/14.
 */
'use strict';

/* Controllers */

var MapController = (function () {

    function MapController($scope, layerService, mapService, oMapNavigatorService, oStationsService) {
        // Initialize Members
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLayerService = layerService;
        this.m_oMapService = mapService;
        this.m_oMapNavigatorService = oMapNavigatorService;
        this.m_oStationsService  = oStationsService;
        this.m_bIsFirstLevel = true;


        // Used in HTML

        // Text to be used on the hover for legend
        this.m_sMapLegendHover = "";
        // Text to set the selected layer
        this.m_sMapLegendSelected = "Mappe";
        // Text to set the selected third level
        this.m_sMapThirdLevelSelected = "";
        // Flag to show or not the third Level
        this.m_bShowThirdLevel = false;
        // Text of the selected station layer
        this.m_sSensorLegendSelected = "Stazioni";

        // Map Links Array
        this.m_aoMapLinks = [];
        // Third Levels Array
        this.m_aoThirdLevels = [];

        // Sensors Array
        this.m_aoSensorsLinks = [];

        // Initialize Layer Service
        if (this.m_oLayerService.getBaseLayers().length == 0) {
            var oBaseLayer1 = new OpenLayers.Layer.Google("Hybrid", {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var oBaseLayer2 = new OpenLayers.Layer.Google("Streets", {numZoomLevels: 20});
            var oBaseLayer3 = new OpenLayers.Layer.Google("Physical", {type: google.maps.MapTypeId.TERRAIN, numZoomLevels: 20});

            this.m_oLayerService.addBaseLayer(oBaseLayer1);
            this.m_oLayerService.addBaseLayer(oBaseLayer2);
            this.m_oLayerService.addBaseLayer(oBaseLayer3);
        }

        // Set map height
        var mapHeight = $("#top").height();// - $("#yr-map-header").outerHeight() - $("#yr-map-footer").outerHeight();
        $("#contentcontainer").height(mapHeight);

        if ($("#omirlMap") != null) {
            $("#omirlMap").height(mapHeight);
        }

        // Initialize Map Link from Navigator Service
        this.m_aoMapLinks = this.m_oMapNavigatorService.getMapFirstLevels();

        this.m_aoSensorsLinks = this.m_oMapNavigatorService.getSensorFirstLevel();
    }


    /**
     * Click on a Icon of the navigator
     * @param oMapLink Map link or null for back
     */
    MapController.prototype.mapLinkClicked = function (oMapLink) {

        if (this.m_bIsFirstLevel && oMapLink == null) return;

        this.m_bShowThirdLevel = false;
        this.m_aoThirdLevels = [];

        this.m_aoMapLinks.forEach(function(oEntry) {
           oEntry.selected = false;
        });

        // Is this a Back click?
        if (oMapLink != null) {
            // Set Selected Layer
            this.m_sMapLegendSelected = oMapLink.description;
        }
        else {
            this.m_sMapLegendSelected = "Mappe";
        }

        // We are in the first level?
        if (this.m_bIsFirstLevel) {
            // Get second level
            this.m_aoMapLinks = this.m_oMapNavigatorService.getMapSecondLevels(oMapLink.linkId);
            // Remember we are in second level
            this.m_bIsFirstLevel = false;
        }
        else {
            // We are in second level
            if (oMapLink == null) {
                // Back: get first levels
                this.m_aoMapLinks = this.m_oMapNavigatorService.getMapFirstLevels();
                this.m_bIsFirstLevel = true;
            }
            else {
                // Switch to show or not third level
                if (oMapLink.hasThirdLevel) {
                    this.m_bShowThirdLevel = true;
                    // Get third levels from the service
                    this.m_aoThirdLevels = this.m_oMapNavigatorService.getMapThirdLevel(oMapLink);

                    // For each level
                    var iLevels;
                    for (iLevels=0; iLevels < this.m_aoThirdLevels.length; iLevels++) {
                        // Is the default?
                        if (this.m_aoThirdLevels[iLevels].isDefault) {
                            // This is the first selected
                            this.m_sMapThirdLevelSelected = this.m_aoThirdLevels[iLevels].description;
                            break;
                        }
                    }
                }

                oMapLink.selected = true;
                // Layer Click
                this.m_sMapLegendSelected = oMapLink.description;
                this.selectedDynamicLayer(oMapLink, "");
            }
        }
    }

    /**
     * Function called when a Dynamic Layer is set
     * @param oMapLink Link to the layer
     * @param sModifier Modifier of the layer Id
     */
    MapController.prototype.selectedDynamicLayer = function (oMapLink, sModifier) {

        // Create WMS Layer
        var oLayer = new OpenLayers.Layer.WMS( oMapLink.description, oMapLink.layerWMS, {layers: oMapLink.layerID+sModifier, transparent: "true", format: "image/png"} );
        oLayer.isBaseLayer = false;

        // Remove last one
        if (this.m_oLayerService.getDynamicLayer() != null) {
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
        }

        // Add the new layer to the map
        this.m_oLayerService.setDynamicLayer(oLayer);
        this.m_oMapService.map.addLayer(oLayer);
    }

    /**
     * Function called when a third level element is clicked
     * @param oThirdLevel
     */
    MapController.prototype.thirdLevelClicked = function (oThirdLevel) {
        this.m_sMapThirdLevelSelected = oThirdLevel.description;
        this.selectedDynamicLayer(oThirdLevel.mapItem, oThirdLevel.layerIDModifier);
    }

    /**
     * Function called when a sensor type is clicked
     * @param oSensorLink
     */
    MapController.prototype.sensorLinkClicked = function (oSensorLink) {

        this.m_aoSensorsLinks.forEach(function(oEntry) {
            oEntry.isActive = false;
        });

        this.m_sSensorLegendSelected = oSensorLink.description;
        oSensorLink.isActive = true;

        this.sensorsLayerSelected(oSensorLink);
    }

    MapController.prototype.getStationsLayerStyle = function() {
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
                strokeWidth: 12,
                pointRadius: 10,
                label: "${value}",
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
                strokeWidth: 12,
                pointRadius: 15,
                label: "${value}",
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
                strokeWidth: 12,
                pointRadius: 20,
                label: "${value}",
                labelOutlineWidth: 1,
                fontColor: "#ffffff",
                fontOpacity: 0.8,
                fontSize: "12px"
            }
        });

        // Create a Style that uses the three previous rules
        var style = new OpenLayers.Style(null, {
            rules: [lowRule, middleRule, highRule]
        });
        return style;
    }

    MapController.prototype.sensorsLayerSelected = function(oSensorLink) {

        // Obtain Stations Values from the server
        var aoStations = this.m_oStationsService.getStations(oSensorLink);

        // Is there a Stations Layer?
        if (this.m_oLayerService.getSensorsLayer() == null) {


            // No: create it
            var oSensorLayer = new OpenLayers.Layer.Vector(oSensorLink.description);
            //oSensorLayer.style = new OpenLayers.StyleMap(this.getStationsLayerStyle());
            this.m_oLayerService.setSensorsLayer(oSensorLayer);
        }
        else {
            // Yes: remove it from the map
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getSensorsLayer());
        }

        // Clear the layer
        this.m_oLayerService.getSensorsLayer().destroyFeatures();

        // Projection change for points
        var  epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = this.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

        // For each station
        var iStations;
        for ( iStations =0; iStations<aoStations.length; iStations++) {
            var oStation = aoStations[iStations];

            var oFeature = new OpenLayers.Feature.Vector(
                new OpenLayers.Geometry.Point(oStation.lon, oStation.lat).transform(epsg4326, projectTo),
                {description: oStation.description},
                {externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
            );

            oFeature.attributes = {
                stationId: oStation.stationId,
                name: oStation.name,
                value: oStation.value
            };

            this.m_oLayerService.getSensorsLayer().addFeatures(oFeature);
        }

        this.m_oMapService.map.addLayer(this.m_oLayerService.getSensorsLayer());
    }

    MapController.prototype.getMapLinks = function () {
        return this.m_aoMapLinks;
    }

    MapController.prototype.getThirdLevels = function() {
        return this.m_aoThirdLevels;
    }

    MapController.prototype.getSensorLinks = function() {
        return this.m_aoSensorsLinks;
    }

    MapController.$inject = [
        '$scope',
        'az.services.layersService',
        'az.services.mapService',
        'MapNavigatorService',
        'StationsService'
    ];

    return MapController;
})();