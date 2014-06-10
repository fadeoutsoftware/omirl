/**
 * Created by p.campanella on 20/01/14.
 */
'use strict';

/* Controllers */

var MapController = (function () {

    function MapController($scope, $window, layerService, mapService, oMapNavigatorService, oStationsService, oDialogService, oChartService) {
        // Initialize Members
        this.m_oScope = $scope;
        this.m_oWindow = $window;
        this.m_oScope.m_oController = this;
        this.m_oLayerService = layerService;
        this.m_oMapService = mapService;
        this.m_oMapNavigatorService = oMapNavigatorService;
        this.m_oStationsService  = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;

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

        // Selected Map Link
        this.m_oSelectedMapLink = null;

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
        //this.m_bIsWeatherActive = true;
        this.m_bIsWeatherActive = false;

        // Flag to know if the side bar is collapsed or not
        this.m_bSideBarCollapsed = false;

        // Initialize Layer Service
        if (this.m_oLayerService.getBaseLayers().length == 0) {
            var oBaseLayer1 = new OpenLayers.Layer.Google("Physical", {type: google.maps.MapTypeId.TERRAIN, numZoomLevels: 20});
            var oBaseLayer2 = new OpenLayers.Layer.Google("Hybrid", {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var oBaseLayer3 = new OpenLayers.Layer.Google("Streets", {numZoomLevels: 20});
            var oBaseLayer4 = new OpenLayers.Layer.Google("Satellite", {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 20});

            /* Base layers inclusion */
            var oOSMLayer = new OpenLayers.Layer.XYZ(
                'OSM',
                'http://www.toolserver.org/tiles/bw-mapnik//${z}/${x}/${y}.png',
                {
                    attribution: 'basemap data &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a>',
                    sphericalMercator: true,
                    wrapDateLine: true,
                    transitionEffect: "resize",
                    buffer: 0,
                    numZoomLevels: 20
                }
            );


            /*
            oBaseLayer1.animationEnabled = false;
            oBaseLayer2.animationEnabled = false;
            oBaseLayer3.animationEnabled = false;
            oBaseLayer4.animationEnabled = false;
            */

            this.m_oLayerService.addBaseLayer(oOSMLayer);
            this.m_oLayerService.addBaseLayer(oBaseLayer1);
            this.m_oLayerService.addBaseLayer(oBaseLayer2);
            this.m_oLayerService.addBaseLayer(oBaseLayer3);
            this.m_oLayerService.addBaseLayer(oBaseLayer4);
        }

        // Set map height
        var mapHeight = $("#top").height();// - $("#yr-map-header").outerHeight() - $("#yr-map-footer").outerHeight();
        $("#contentcontainer").height(mapHeight);

        if ($("#omirlMap") != null) {
            $("#omirlMap").height(mapHeight);
        }

        var oServiceVar = this;

        this.m_oMapNavigatorService.fetchMapFirstLevels();

        this.m_oMapNavigatorService.getSensorFirstLevel().success(function (data, status) {

            for (var iElement = 0; iElement < data.length; iElement++) {
                oServiceVar.m_aoSensorsLinks.push(data[iElement]);
            }
        }).error(function (data, status) {
            alert('Error Loading Sensors Items to add to the Menu');
        });


        this.m_oMapNavigatorService.getStaticLayerLinks().success(function (data, status) {

            for (var iElement = 0; iElement < data.length; iElement++) {
                oServiceVar.m_aoStaticLinks.push(data[iElement]);
            }
        }).error(function (data, status) {
            alert('Error Loading Static Layers to add to the Menu');
        });


        // TODO: Disabilito per ora il layer di default
        //this.m_oMapService.callbackArg = this;
        //this.m_oMapService.readyCallback = this.AddWeatherLayer;


        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oServiceVar.m_oMapService.map != null) {
                oServiceVar.m_oLayerService.clarAll();
                oServiceVar.m_oMapService.map.destroy();
                oServiceVar.m_oMapService.map = null;
                oServiceVar.m_oMapService.stationsPopupControllerAdded = false;
            }
        });
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
               oMapService.map.setCenter([oPoint.x, oPoint.y],12);

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

        // Map Link = null stands for back: impossible to have back on first level
        if (this.m_bIsFirstLevel && oMapLink == null) return;

        // Clear member variables
        this.m_bShowThirdLevel = false;
        this.m_aoThirdLevels = [];

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Is this a Map Link Click?
        if (oMapLink != null) {
            // Write on screen the Selected Layer description
            if (!this.m_bDynamicLayerActive && this.m_bIsFirstLevel) this.m_sMapLegendSelected = oMapLink.description;
            // Remember if it was selected or not
            bIsSelected = oMapLink.selected;
        }
        else {
            // No is a Back Click
            if (!this.m_bDynamicLayerActive) this.m_sMapLegendSelected = "Mappe";
        }

        // Clear all selection flags
        this.m_aoMapLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });


        // We are in the first level?
        if (this.m_bIsFirstLevel) {

            // Remember we are in second level
            this.m_bIsFirstLevel = false;

            // Clear variables and remember the Controller ref
            var oControllerVar = this;
            oControllerVar.m_aoMapLinks = [];

            // Get second level from server
            this.m_oMapNavigatorService.getMapSecondLevels(oMapLink.linkId).success(function(data,status) {

                // Second Level Icons
                oControllerVar.m_aoMapLinks = data;

                // Is there any Map selected?
                if (oControllerVar.m_oSelectedMapLink != null) {

                    //Is One of these links the one selected?
                    var iCount;
                    for (iCount = 0; iCount< oControllerVar.m_aoMapLinks.length; iCount++) {

                        // Check by Layer Id
                        if (oControllerVar.m_aoMapLinks[iCount].layerID == oControllerVar.m_oSelectedMapLink.layerID) {

                            // This is the selected one!!
                            oControllerVar.setSelectedMapLinkOnScreen(oControllerVar, oControllerVar.m_aoMapLinks[iCount]);

                            // Switch to show or not third level
                            if (oControllerVar.m_aoMapLinks[iCount].hasThirdLevel) {

                                // Get third levels from the service
                                oControllerVar.m_oMapNavigatorService.getMapThirdLevel(oControllerVar.m_aoMapLinks[iCount]).success(function(data,status) {
                                    oControllerVar.gotMapThirdLevelFromServer(data, status,oControllerVar,oControllerVar.m_aoMapLinks[iCount]);
                                }).error(function(data,status){
                                    alert('Error Contacting Omirl Server');
                                });

                            }
                            else {
                                oControllerVar.m_sMapThirdLevelSelectedModifier = "";
                            }

                            break;
                        }
                    }

                }
            }).error(function(data,status){
                alert('Error Contacting Omirl Server');
            });

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

                    var oMapLinkCopy = oMapLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    this.m_oMapNavigatorService.getMapThirdLevel(oMapLink).success(function(data,status) {

                        oControllerVar.gotMapThirdLevelFromServer(data, status,oControllerVar,oMapLinkCopy);
                    }).error(function(data,status){
                        alert('Error Contacting Omirl Server');
                    });

                }
                else {
                    this.m_sMapThirdLevelSelectedModifier = "";
                }

                if (!bIsSelected) {

                    this.setSelectedMapLinkOnScreen(this,oMapLink);
                    this.selectedDynamicLayer(oMapLink, this.m_sMapThirdLevelSelectedModifier);
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
                    this.m_sMapLegendTooltip = "Legenda Mappa";
                    this.m_sMapLegendIconPath = "";

                    this.m_oSelectedMapLink = null;
                }
            }
        }
    }

    /**
     * Sets all needed variables to show selected Map Link on screen
     * @param oControllerVar
     * @param oMapLink
     */
    MapController.prototype.setSelectedMapLinkOnScreen = function (oControllerVar, oMapLink) {

        oMapLink.selected = true;
        // Layer Click
        oControllerVar.m_sMapLegendIconPath = oMapLink.link;
        oControllerVar.m_sMapLegendTooltip = "Legenda " + oMapLink.description;
        oControllerVar.m_sMapLegendSelected = oMapLink.description;
        oControllerVar.m_bDynamicLayerActive = true;
        oControllerVar.m_sMapLegendPath = oMapLink.legendLink;
        oControllerVar.m_oSelectedMapLink = oMapLink;
    }


    /**
     * Callback called when the third level options are read from the server
     * Fills the combo and find the selected layer
     * @param data
     * @param status
     * @param oControllerVar
     * @param oMapLinkCopy
     */
    MapController.prototype.gotMapThirdLevelFromServer = function(data,status, oControllerVar, oMapLinkCopy) {
        oControllerVar.m_aoThirdLevels = data;
        oControllerVar.m_bShowThirdLevel = true;
        // For each level
        var iLevels;
        for (iLevels=0; iLevels < oControllerVar.m_aoThirdLevels.length; iLevels++) {
            oControllerVar.m_aoThirdLevels[iLevels].mapItem = oMapLinkCopy;
            // Is the default?
            if (oControllerVar.m_aoThirdLevels[iLevels].layerIDModifier == oControllerVar.m_sMapThirdLevelSelectedModifier) {
                // This is the first selected
                oControllerVar.m_sMapThirdLevelSelected = oControllerVar.m_aoThirdLevels[iLevels].description;
                //break;
            }
        }
    };

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
            this.m_sSensorsLegendIconPath = oSensorLink.imageLinkOff;
            this.m_sSensorLegendTooltip = "Legenda " + oSensorLink.description;
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
                this.m_sSensorsLegendIconPath = "";
                this.m_sSensorLegendTooltip = "";
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
            sTimeDelta += iDeltaMinutes + " min. fa";
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
        //var iNum = parseInt(oFeature.attributes.referenceDate.replace(/[^0-9]/g, ""));
        // Create Reference Date
        //var oReferenceDate = new Date(iNum);
        var oReferenceDate = new Date(oFeature.attributes.referenceDate);

        // Compute time delta
        var sTimeDelta = this.getDelayString(oReferenceDate);

        //var iMonth = oReferenceDate.getMonth() + 1;
        var iMonth = oReferenceDate.getUTCMonth() + 1;

        //var sMinutes = "" + oReferenceDate.getMinutes();
        var sMinutes = "" + oReferenceDate.getUTCMinutes();

        if (sMinutes.length<2) {
            sMinutes = "0" + sMinutes;
        }

        // Write reference date text
        //var sReferenceData = oReferenceDate.getDate() + "/" + iMonth + "/" + oReferenceDate.getFullYear() + " - " + oReferenceDate.getHours() + ":" + sMinutes;
        var sReferenceData = oReferenceDate.getUTCDate() + "/" + iMonth + "/" + oReferenceDate.getUTCFullYear() + " - " + oReferenceDate.getUTCHours() + ":" + sMinutes + " UTC";

        // Start Pop up HTML
        var sHtml = "<div class='stationsPopupPanel'>";

        // Stations Informations
        sHtml += "<strong style=\"font-size: 14px;\">"+oFeature.attributes.municipality+"</strong><br>";

        // Stations Informations
        sHtml += "<p><strong>" + oFeature.attributes.name + " [" + oFeature.attributes.altitude + "  s.l.m.]" + "</strong></p>";

        var sValue = parseFloat(oFeature.attributes.value ).toFixed(2);

        // Sensor Value
        sHtml +="<h4>"+"<img class='stationsPopupImage' src='"+oFeature.attributes.imageLinkInv+"' style=\"font-family: 'Glyphicons Halflings';font-size: 16px;border: 1px solid #ffffff;padding: 2px;border-radius: 3px;\"/> " + sValue + " " + oFeature.attributes.measureUnit + "</h4>";

        // Time reference
        sHtml += "<p><span class='popupglyphicon glyphicon glyphicon-time' style=\"font-family: 'Glyphicons Halflings';font-size: 15px;\"></span> " + sTimeDelta + " " + sReferenceData + "</p>";

        // Station Code
        sHtml += "<br><div>Codice: " + oFeature.attributes.shortCode + "</div>";
        // Lat Lon
        sHtml += "<div>Lat: " + oFeature.attributes.lat + " Lon: " + oFeature.attributes.lon + "</div>";

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
        var oSize = new OpenLayers.Size(190,190);

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
        oPopUp.autoSize = false;

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
        //alert('Ora compare un grafico bellissimo!');

        var oControllerVar = this;
        var sStationCode = oFeature.attributes.shortCode;

        if (this.m_oDialogService.isExistingDialog(sStationCode)) {
            return;
        }

        var sSensorType = "Pluvio";

        // Reset all actives flag
        this.m_aoSensorsLinks.forEach(function(oEntry) {
            if (oEntry.isActive) {
                sSensorType = oEntry.code;
            }
        });

        // The data for the dialog
        var model = {
                "stationCode": sStationCode,
                "chartType": sSensorType
            };


        // jQuery UI dialog options
        var options = {
            autoOpen: false,
            modal: false,
            width: 600,
            close: function(event, ui) {
                // Remove the chart from the Chart Service
                oControllerVar.m_oChartService.removeChart(sStationCode);
            }
        };

        this.m_oDialogService.open(sStationCode,"stationsChart.html", model, options)
    }

    MapController.prototype.compareStations = function(oFirst, oSecond) {
        if (oFirst.value < oSecond.value)
            return -1;
        if (oFirst.value > oSecond.value)
            return 1;
        return 0;
    }

    /**
     * Function called to show the selected sensor layer
     * @param oSensorLink
     */
    MapController.prototype.showStationsLayer = function(oSensorLink) {

        var oServiceVar = this;

        // Obtain Stations Values from the server
        this.m_oStationsService.getStations(oSensorLink).success(function(data,status) {

            var aoStations = data;

            try{
                // remove the actual Sensors Layer from the map
                oServiceVar.m_oMapService.map.removeLayer(oServiceVar.m_oLayerService.getSensorsLayer());
            }
            catch (err) {

            }

            // Clear the layer
            oServiceVar.m_oLayerService.getSensorsLayer().destroyFeatures();

            // Projection change for points
            var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
            var projectTo = oServiceVar.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

            // For each station
            var iStations;
            var aoFeatures = [];

            aoStations.sort(oServiceVar.compareStations);

            for ( iStations =0; iStations<aoStations.length; iStations++) {
                var oStation = aoStations[iStations];

                // Create the feature
                var oFeature = new OpenLayers.Feature.Vector(
                    new OpenLayers.Geometry.Point(oStation.lon, oStation.lat).transform(epsg4326, projectTo),
                    {description: oStation.description}
                    //,{externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
                    //,{title: oStation.name + " " + oStation.value }
                );

                // Get Increment by server
                var iIncrement = oStation.increment;

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
                    imageLinkInv: oSensorLink.imageLinkInv,
                    // Sensor Type
                    sensorType: oSensorLink.code,
                    // Increment
                    increment: iIncrement,
                    // Municipality
                    municipality: oStation.municipality,
                    // Image Path
                    imgPath: oStation.imgPath
                };

                // Add the feature to the array
                aoFeatures.push(oFeature);
            }

            // Add feature array to the layer
            oServiceVar.m_oLayerService.getSensorsLayer().addFeatures(aoFeatures);

            // Add the layer to the map
            oServiceVar.m_oMapService.map.addLayer(oServiceVar.m_oLayerService.getSensorsLayer());
            oServiceVar.m_oMapService.map.setLayerIndex(oServiceVar.m_oLayerService.getSensorsLayer(), oServiceVar.m_oLayerService.getSensorsLayerIndex());

            // Feature Click and Hover Control: added?
            if (oServiceVar.m_oMapService.stationsPopupControllerAdded == false) {

                // No: take a reference to the map controller
                var oMapController = oServiceVar;

                // Create the Control
                var oPopupCtrl = new OpenLayers.Control.SelectFeature(oServiceVar.m_oLayerService.getSensorsLayer(), {
                    hover: true,
                    onSelect: function(feature) {
                        // Hover Select: call internal function
                        oMapController.showStationsPopup(feature);
                    },
                    onUnselect: function(feature) {
                        // Hover Unselect: remove pop u
                        // TODO POPUP
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
                oServiceVar.m_oMapService.map.addControl(oPopupCtrl);
                oPopupCtrl.activate();

                // Remember it exists now
                oServiceVar.m_oMapService.stationsPopupControllerAdded = true;
            }
        }).error(function(data,status){
            alert('Error Contacting Omirl Server');
        });

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

        if (this.m_bIsFirstLevel) {
            this.m_aoMapLinks = this.m_oMapNavigatorService.getMapFirstLevels();
        }

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

    MapController.prototype.toggleSideBarClicked = function() {

        var oElement = angular.element("#mapNavigation");

        if (oElement != null) {
            if (oElement.length>0) {
                var iWidth = oElement[0].clientWidth;
                iWidth -= 0;

                if (!this.m_bSideBarCollapsed) {
                    oElement[0].style.left = "-" + iWidth + "px";
                }
                else {
                    oElement[0].style.left =  "0px";
                }

                //oElement.sty
            }
        }

        this.m_bSideBarCollapsed = !this.m_bSideBarCollapsed;
    }

    MapController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    MapController.prototype.resetZoomClick = function () {

        // Transformations objects
        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = this.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

        // Tranform the point
        //var oPoint = new OpenLayers.Geometry.Point(8.60,44.20).transform(epsg4326, projectTo);

        //var oProjection = 'EPSG:4326';
        var oCenter = new OpenLayers.LonLat(8.60,44.20).transform(epsg4326, projectTo);
        this.m_oMapService.map.setCenter(oCenter, 9);
    }

    MapController.$inject = [
        '$scope',
        '$window',
        'az.services.layersService',
        'az.services.mapService',
        'MapNavigatorService',
        'StationsService',
        'dialogService',
        'ChartService'
    ];

    return MapController;
})();