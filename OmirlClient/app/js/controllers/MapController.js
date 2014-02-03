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

        // Flag to know if first level is shown
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

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Is this a Back click?
        if (oMapLink != null) {
            // Set Selected Layer
            this.m_sMapLegendSelected = oMapLink.description;
            bIsSelected = oMapLink.selected;
        }
        else {
            this.m_sMapLegendSelected = "Mappe";
        }

        // Clear all selection flags
        this.m_aoMapLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });


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

                if (!bIsSelected) {
                    oMapLink.selected = true;
                    // Layer Click
                    this.m_sMapLegendSelected = oMapLink.description;
                    this.selectedDynamicLayer(oMapLink, "");
                }
                else {
                    // Remove from the map
                    if (this.m_oLayerService.getDynamicLayer() != null) {
                        this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
                        this.m_oLayerService.setDynamicLayer(null);
                    }
                    this.m_sMapLegendSelected = "";
                }
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
        this.m_oMapService.map.setLayerIndex(oLayer,this.m_oLayerService.getBaseLayers().length);
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

        // Check if the sensor link is active
        if (!oSensorLink.isActive){
            // Set the textual description
            this.m_sSensorLegendSelected = oSensorLink.description;

            // Reset all actives flag
            this.m_aoSensorsLinks.forEach(function(oEntry) {
                oEntry.isActive = false;
            });

            // Set this as the active one
            oSensorLink.isActive = true;

            // Set
            this.showStationsLayer(oSensorLink);
        }
        else {
            // Set the textual description
            this.m_sSensorLegendSelected = "";

            oSensorLink.isActive = false;
            try{
                // remove the Sensors Layer from the map
                this.m_oMapService.map.removeLayer(this.m_oLayerService.getSensorsLayer());
            }
            catch (err) {

            }
        }


    }


    /**
     * Function called to show the selected sensor layer
     * @param oSensorLink
     */
    MapController.prototype.showStationsLayer = function(oSensorLink) {

        // Obtain Stations Values from the server
        var aoStations = this.m_oStationsService.getStations(oSensorLink);

        try{
            // remove the actual Sensors Layer from the map
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getSensorsLayer());
        }
        catch (err) {

        }

        // Clear the layer
        this.m_oLayerService.getSensorsLayer().destroyFeatures();

        // Projection change for points
        var  epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = this.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

        // For each station
        var iStations;
        var aoFeatures = [];
        for ( iStations =0; iStations<aoStations.length; iStations++) {
            var oStation = aoStations[iStations];

            // Create the feature
            var oFeature = new OpenLayers.Feature.Vector(
                new OpenLayers.Geometry.Point(oStation.lon, oStation.lat).transform(epsg4326, projectTo),
                {description: oStation.description}
                //,{externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
                //,{title: oStation.name + " " + oStation.value }
            );

            // Set attributes
            oFeature.attributes = {
                stationId: oStation.stationId,
                name: oStation.name,
                value: oStation.value
            };

            // Add the feature to the array
            aoFeatures.push(oFeature);
        }

        // Add feature array to the layer
        this.m_oLayerService.getSensorsLayer().addFeatures(aoFeatures);

        // Add the layer to the map
        this.m_oMapService.map.addLayer(this.m_oLayerService.getSensorsLayer());
        this.m_oMapService.map.setLayerIndex(this.m_oLayerService.getSensorsLayer(), this.m_oLayerService.getSensorsLayerIndex());
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