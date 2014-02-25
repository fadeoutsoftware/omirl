/**
 * Created by p.campanella on 20/01/14.
 */
'use strict';

/* Controllers */

var MapController = (function () {

    function MapController($scope, $window, layerService, mapService, oMapNavigatorService, oStationsService) {
        // Initialize Members
        this.m_oScope = $scope;
        this.m_oWindow = $window;
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
        // Path of the map legend image
        this.m_sMapLegendPath = "";
        // Path of the sensors legend image
        this.m_sSensorsLegendPath = "";

        // Test of the Geocoding Query
        this.m_sGeocodingQuery = "";
        // Flag to know if a map is active
        this.m_bDynamicLayerActive = false;
        // Flag to Know if a Station Layer is active
        this.m_bSensorLayerActive = false;
        // Flag to Know if the map legend image is to be shown
        this.m_bShowMapLegendImage = true;
        // Flag to Know if the sensors legend image is to be shown
        this.m_bShowSensorsLegendImage = true;

        // Remembers the actual selected third level modifier
        this.m_sMapThirdLevelSelectedModifier = "";

        // Map Links Array
        this.m_aoMapLinks = [];
        // Third Levels Array
        this.m_aoThirdLevels = [];

        // Sensors Array
        this.m_aoSensorsLinks = [];

        // Statics Array
        this.m_aoStaticLinks = [];

        // Weather Variables
        this.m_bIsWeatherActive = true;

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

        this.m_aoStaticLinks = this.m_oMapNavigatorService.getStaticLayerLinks();

        this.m_oMapService.callbackArg = this;
        this.m_oMapService.readyCallback = this.AddWeatherLayer;
    }

    MapController.prototype.setBaseLayer = function(sCode) {
        var oBaseLayer = this.m_oMapService.map.getLayersByName(sCode)[0];
        this.m_oMapService.map.setBaseLayer(oBaseLayer);
    }

    /**
     * Function called when the user types on the search text box: handles the enter key
     * @param oEvent
     */
    MapController.prototype.geocodingEnterSearch = function(oEvent) {
        if (oEvent.which==13) {
            this.geocodingSearch();
        }
    }

    /**
     * Called from the enter key or the search icon
     * executes geocoding, adds the marker and set the map center
     */
    MapController.prototype.geocodingSearch = function() {

        // create the geocoder
        var oGeoCoder = new google.maps.Geocoder();

        // get query and services
        var sGeocodingQuery = this.m_sGeocodingQuery;
        var oMapService = this.m_oMapService;
        var oLayerService = this.m_oLayerService;

        // Is this an empty query?
        if (sGeocodingQuery == "") {
            try{
                // remove the Sensors Layer from the map
                oMapService.map.removeLayer(oLayerService.getMarkerLayer());
            }
            catch (err) {

            }

            return;
        }

        // Decode the request
        oGeoCoder.geocode( {'address': sGeocodingQuery}, function(aoResults, oStatus) {

           if (oStatus == google.maps.GeocoderStatus.OK) {
               // Get the location
               var oLocation = new OpenLayers.LonLat(aoResults[0].geometry.location.lng(), aoResults[0].geometry.location.lat());
               // Transformations objects
               var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
               var projectTo = oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

               // Tranform the point
               var oPoint = new OpenLayers.Geometry.Point(oLocation.lon, oLocation.lat).transform(epsg4326, projectTo);
               // Set the center
               oMapService.map.setCenter([oPoint.x, oPoint.y],16);

               var oIconSize = new OpenLayers.Size(48,48);
               var oIconOffset = new OpenLayers.Pixel(-(oIconSize.w/2), -oIconSize.h);
               var oIcon = new OpenLayers.Icon('img/marker.png',oIconSize,oIconOffset);
               var oMarker = new OpenLayers.Marker(oLocation.transform(epsg4326,projectTo), oIcon);
               try{
                   // remove the Sensors Layer from the map
                   oMapService.map.removeLayer(oLayerService.getMarkerLayer());
               }
               catch (err) {

               }

               oLayerService.getMarkerLayer().clearMarkers();
               oLayerService.getMarkerLayer().addMarker(oMarker);
               oMapService.map.addLayer(oLayerService.getMarkerLayer());
           }
        });
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
            if (!this.m_bDynamicLayerActive && this.m_bIsFirstLevel) this.m_sMapLegendSelected = oMapLink.description;
            bIsSelected = oMapLink.selected;
        }
        else {
            if (!this.m_bDynamicLayerActive) this.m_sMapLegendSelected = "Mappe";
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
                        if (this.m_aoThirdLevels[iLevels].layerIDModifier == this.m_sMapThirdLevelSelectedModifier) {
                            // This is the first selected
                            this.m_sMapThirdLevelSelected = this.m_aoThirdLevels[iLevels].description;
                            break;
                        }
                    }
                }
                else {
                    this.m_sMapThirdLevelSelectedModifier = "";
                }

                if (!bIsSelected) {
                    oMapLink.selected = true;
                    // Layer Click
                    this.m_sMapLegendSelected = oMapLink.description;
                    this.selectedDynamicLayer(oMapLink, this.m_sMapThirdLevelSelectedModifier);
                    this.m_bDynamicLayerActive = true;
                    this.m_sMapLegendPath = oMapLink.legendLink;
                }
                else {
                    // Remove from the map
                    if (this.m_oLayerService.getDynamicLayer() != null) {
                        this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
                        this.m_oLayerService.setDynamicLayer(null);
                    }
                    this.m_sMapLegendSelected = "";
                    this.m_bShowThirdLevel = false;
                    this.m_bDynamicLayerActive = false;
                    this.m_sMapLegendPath = "";
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

        oLayer.setOpacity(0.6);
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
        // Save actual description
        this.m_sMapThirdLevelSelected = oThirdLevel.description;
        // Save actual modifier
        this.m_sMapThirdLevelSelectedModifier = oThirdLevel.layerIDModifier;
        // Show the layer
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
            this.m_bSensorLayerActive = true;
            this.m_sSensorsLegendPath = oSensorLink.legendLink;
        }
        else {
            // Set the textual description
            this.m_sSensorLegendSelected = "";

            oSensorLink.isActive = false;
            this.m_sSensorsLegendPath = "";
            try{
                // remove the Sensors Layer from the map
                this.m_oMapService.map.removeLayer(this.m_oLayerService.getSensorsLayer());
                this.m_bSensorLayerActive = false;
            }
            catch (err) {

            }
        }
    }

    /**
     * Gets a string indicating the delay from oReferenceDate vs Now
     * @param oReferenceDate
     * @returns {string}
     */
    MapController.prototype.getDelayString = function(oReferenceDate) {

        // Get Now
        var oDate = new Date();

        // Compute time difference
        var iDifference = oDate.getTime()-oReferenceDate.getTime();

        // How it is in minutes?
        var iMinutes = 1000*60;

        var iDeltaMinutes = Math.round(iDifference/iMinutes);

        var sTimeDelta = "";

        if (iDeltaMinutes<60) {
            // Less then 1h
            sTimeDelta += iDeltaMinutes + " minuti fa";
        }
        else if (iDeltaMinutes< 60*24) {
            // Less then 1d
            var iDeltaHours =  Math.round(iDeltaMinutes/60);
            sTimeDelta += iDeltaHours + " ore fa";
        }
        else {
            // More than 1d
            var iDeltaDays =  Math.round(iDeltaMinutes/(60*24));
            sTimeDelta += iDeltaDays + " giorni fa";
        }

        return sTimeDelta;
    }

    /**
     * Gets the HTML content of the pop up
     * @param oFeature
     * @returns {string}
     */
    MapController.prototype.getStationPopupContent = function(oFeature) {
        // Get the time value from the Json Date Rapresentation
        var iNum = parseInt(oFeature.attributes.referenceDate.replace(/[^0-9]/g, ""));
        // Create Reference Date
        var oReferenceDate = new Date(iNum);

        // Compute time delta
        var sTimeDelta = this.getDelayString(oReferenceDate);

        // Write reference date text
        var sReferenceData = oReferenceDate.getDate() + "/" + oReferenceDate.getMonth() + "/" + oReferenceDate.getFullYear() + " - " + oReferenceDate.getHours() + ":" + oReferenceDate.getMinutes();

        // Start Pop up HTML
        var sHtml = "<div class='stationsPopupPanel'>";


        // Stations Informations
        sHtml += "<p><strong>" + oFeature.attributes.shortCode + " - " + oFeature.attributes.name + " [" + oFeature.attributes.altitude + "]" + "</strong></p>";

        // Sensor Value
        sHtml +="<h2>"+"<img class='stationsPopupImage' src='"+oFeature.attributes.imageLinkInv+"' style=\"font-family: 'Glyphicons Halflings';font-size: 25px;\"/> " + oFeature.attributes.value + " " + oFeature.attributes.measureUnit + "</h2>";

        // Time reference
        sHtml += "<p><span class='popupglyphicon glyphicon glyphicon-time' style=\"font-family: 'Glyphicons Halflings';font-size: 15px;\"></span> " + sTimeDelta + " " + sReferenceData + "</p>";

        // Lat Lon
        sHtml += "<p><em> Lat: " + oFeature.attributes.lat + " Lon: " + oFeature.attributes.lon + "</em></p>";

        // Close Popup Div
        sHtml += "</div>";

        // Return HTML
        return sHtml;
    }


    /**
     * Function called to show station pop up
     * @param oFeature
     */
    MapController.prototype.showStationsPopup = function(oFeature) {

        // Get Pop Up HTML
        var sHtml = this.getStationPopupContent(oFeature);
        // Dummy size
        var oSize = new OpenLayers.Size(250,100);

        // Create Popup
        var oPopUp = new OpenLayers.Popup(
            oFeature.id + "_popup",
            oFeature.geometry.getBounds().getCenterLonLat(),
            oSize,
            sHtml,
            false,
            null
        );

        // Opacity and back color
        oPopUp.setOpacity(0.8);
        oPopUp.setBackgroundColor("#000000");
        // Auto size please
        oPopUp.autoSize = true;

        // Set the popup to the feature
        oFeature.popup = oPopUp;

        // Add Popup to the map
        oFeature.layer.map.addPopup(oPopUp);
    }

    /**
     * Function called when a station feature is clicked
     * @param oFeature
     */
    MapController.prototype.showStationsChart = function(oFeature) {
        alert('Ora compare un grafico bellissimo!');
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
        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
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


            // Set attributes of the Feature
            oFeature.attributes = {
                // Station Id
                stationId: oStation.stationId,
                // Station Name
                name: oStation.name,
                // Sensor Value
                value: oStation.value,
                // Reference Date
                referenceDate: oStation.refDate,
                // Measure Unit
                measureUnit: oSensorLink.mesUnit,
                // Other Html content for the pop up, received from the server
                otherHtml: oStation.otherHtml,
                // Altitude
                altitude: oStation.alt,
                // Coordinates
                lat: oStation.lat,
                lon: oStation.lon,
                // Station Code
                shortCode: oStation.shortCode,
                // Image Link to use in the popup
                imageLinkInv: oSensorLink.imageLinkInv
            };

            // Add the feature to the array
            aoFeatures.push(oFeature);
        }

        // Add feature array to the layer
        this.m_oLayerService.getSensorsLayer().addFeatures(aoFeatures);

        // Add the layer to the map
        this.m_oMapService.map.addLayer(this.m_oLayerService.getSensorsLayer());
        this.m_oMapService.map.setLayerIndex(this.m_oLayerService.getSensorsLayer(), this.m_oLayerService.getSensorsLayerIndex());

        // Feature Click and Hover Control: added?
        if (this.m_oMapService.stationsPopupControllerAdded == false) {

            // No: take a reference to the map controller
            var oMapController = this;

            // Create the Control
            var oPopupCtrl = new OpenLayers.Control.SelectFeature(this.m_oLayerService.getSensorsLayer(), {
                hover: true,
                onSelect: function(feature) {
                    // Hover Select: call internal function
                    oMapController.showStationsPopup(feature);
                },
                onUnselect: function(feature) {
                    // Hover Unselect: remove pop up
                    feature.layer.map.removePopup(feature.popup);
                },
                callbacks: {
                    // Click
                    click: function(feature) {
                        // Show chart
                        oMapController.showStationsChart(feature);
                    }
                }
            });

            // Add and activate the control
            this.m_oMapService.map.addControl(oPopupCtrl);
            oPopupCtrl.activate();

            // Remember it exists now
            this.m_oMapService.stationsPopupControllerAdded = true;
        }
    }

    /**
     * Called when a static layer is clicked
     * @param oStatic
     */
    MapController.prototype.staticLayerClicked = function(oStatic) {

        if (oStatic.selected) {
            // Remove the layer from the map
            try {
                var oRemovingLayer = this.m_oMapService.map.getLayersByName(oStatic.layerID)[0];
                this.m_oMapService.map.removeLayer(oRemovingLayer);
                this.m_oLayerService.removeStaticLayer(oRemovingLayer);
            }
            catch (err) {

            }
        }
        else {
            // Add the layer to the map
            // Create WMS Layer
            var oLayer = new OpenLayers.Layer.WMS( oStatic.layerID, oStatic.layerWMS, {layers: oStatic.layerID, transparent: "true", format: "image/png"} );
            oLayer.isBaseLayer = false;
            this.m_oLayerService.addStaticLayer(oLayer);
            this.m_oMapService.map.addLayer(oLayer);
        }

        oStatic.selected = !oStatic.selected;
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

    MapController.prototype.getStaticLinks = function() {
        return this.m_aoStaticLinks;
    }

    /**
     * Method that can be called to obtain the width center (TODO to finish...)
     * @returns {number}
     */
    MapController.prototype.getLegendRight = function() {
        var iWidth = this.m_oWindow.innerWidth;
        return iWidth/2;
    }

    /**
     * Method called when the user clicks on the map legend Icon: switches show flag of the legend image
     */
    MapController.prototype.mapLegendClicked = function() {
        this.m_bShowMapLegendImage = !this.m_bShowMapLegendImage;
    }

    /**
     *  Method called when the user clicks on the sensor legend Icon: switches show flag of the legend image
     */
    MapController.prototype.sensorsLegendClicked = function() {
        this.m_bShowSensorsLegendImage = !this.m_bShowSensorsLegendImage;
    }

    MapController.prototype.ToggleWeatherLayer = function() {
        if  (this.m_bIsWeatherActive)  {
            this.m_bIsWeatherActive = false;
            this.RemoveWeatherLayer();
        }
        else {
            this.m_bIsWeatherActive = true;
            this.AddWeatherLayer(this);
        }
    }

    MapController.prototype.AddWeatherLayer = function(oMapController) {

        // Obtain Stations Values from the server
        var aoStations = oMapController.m_oStationsService.getWeather();

        try{
            // remove the actual Sensors Layer from the map
            oMapController.m_oMapService.map.removeLayer(oMapController.m_oLayerService.getWeatherLayer());
        }
        catch (err) {

        }

        // Clear the layer
        oMapController.m_oLayerService.getWeatherLayer().destroyFeatures();

        // Add the layer to the map
        oMapController.m_oMapService.map.addLayer(oMapController.m_oLayerService.getWeatherLayer());
        oMapController.m_oMapService.map.setLayerIndex(oMapController.m_oLayerService.getWeatherLayer(), oMapController.m_oLayerService.getWeatherLayerIndex());


        // Projection change for points
        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = oMapController.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

        // For each station
        var iStations;
        var aoFeatures = [];
        for ( iStations =0; iStations<aoStations.length; iStations++) {
            var oStation = aoStations[iStations];

            // Create the feature
            var oFeature = new OpenLayers.Feature.Vector(
                new OpenLayers.Geometry.Point(oStation.lon, oStation.lat).transform(epsg4326, projectTo),
                {},
                {externalGraphic: oStation.imgPath, graphicHeight: 48, graphicWidth: 48, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
                //,{title: oStation.name + " " + oStation.value }
            );


            // Set attributes of the Feature
            oFeature.attributes = {
                // Station Id
                stationId: oStation.stationId,
                // Station Name
                name: oStation.name,
                // Sensor Value
                value: oStation.value,
                // Reference Date
                referenceDate: oStation.refDate,
                // Measure Unit
                measureUnit: '',
                // Other Html content for the pop up, received from the server
                otherHtml: oStation.otherHtml,
                // Altitude
                altitude: oStation.alt,
                // Coordinates
                lat: oStation.lat,
                lon: oStation.lon,
                // Station Code
                shortCode: oStation.shortCode,
                // Image Link to use in the popup
                imageLinkInv: ''
            };

            // Add the feature to the array
            aoFeatures.push(oFeature);
        }

        // Add feature array to the layer
        oMapController.m_oLayerService.getWeatherLayer().addFeatures(aoFeatures);

        /*
        // Feature Click and Hover Control: added?
        if (this.m_oMapService.stationsPopupControllerAdded == false) {

            // No: take a reference to the map controller
            var oMapController = this;

            // Create the Control
            var oPopupCtrl = new OpenLayers.Control.SelectFeature(this.m_oLayerService.getSensorsLayer(), {
                hover: true,
                onSelect: function(feature) {
                    // Hover Select: call internal function
                    oMapController.showStationsPopup(feature);
                },
                onUnselect: function(feature) {
                    // Hover Unselect: remove pop up
                    feature.layer.map.removePopup(feature.popup);
                },
                callbacks: {
                    // Click
                    click: function(feature) {
                        // Show chart
                        oMapController.showStationsChart(feature);
                    }
                }
            });

            // Add and activate the control
            this.m_oMapService.map.addControl(oPopupCtrl);
            oPopupCtrl.activate();

            // Remember it exists now
            this.m_oMapService.stationsPopupControllerAdded = true;
        }*/
    }

    MapController.prototype.RemoveWeatherLayer = function() {
        try{
            // remove the actual Sensors Layer from the map
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getWeatherLayer());
        }
        catch (err) {

        }
    }

    MapController.$inject = [
        '$scope',
        '$window',
        'az.services.layersService',
        'az.services.mapService',
        'MapNavigatorService',
        'StationsService'
    ];

    return MapController;
})();