/**
 * Created by p.campanella on 20/01/14.
 */
'use strict';

/* Controllers */

var MapController = (function () {

    function MapController($scope, $window, layerService, mapService, oMapNavigatorService, oStationsService, oDialogService, oChartService, oConstantsService, $interval, $log, $location) {
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
        this.m_oConstantsService = oConstantsService;
        this.m_oInterval = $interval;
        this.m_oLog =$log;
        this.m_oLocation = $location;

        // Flag to know if maps first level is shown
        this.m_bIsFirstLevel = true;
        // Flag to know if hydro first level is shown
        this.m_bIsHydroFirstLevel = true;
        this.m_iHydroLevel = 1;


        // Used in HTML

        // Text to be used on the hover for Map legend
        this.m_sMapLegendHover = "";
        // Text to set the selected Map layer
        this.m_sMapLegendSelected = "Mappe";
        // Text to set the selected Map third level
        this.m_sMapThirdLevelSelected = "";
        // Flag to show or not the Map third Level
        this.m_bShowThirdLevel = false;
        // Text of the selected station layer
        this.m_sSensorLegendSelected = "Stazioni";
        // Path of the map legend image
        this.m_sMapLegendPath = "";
        // Path of the sensors legend image
        this.m_sSensorsLegendPath = "";
        // Text to be used on the hover for Hydro legend
        this.m_sHydroLegendHover = "";
        // Text to set the selected Hydro layer
        this.m_sHydroLegendSelected = "Modelli";
        // Last hydro selected legend text used when the user goes back
        this.m_sHydroLastLegendSelected = "Modelli";
        // Path of the Idro Legend image
        this.m_sMapLegendPath = "";
        // Tootltip of the hydro legent
        this.m_sMapLegendTooltip = "Legenda Idro";
        // Path of the hydro legend icon image
        this.m_sMapLegendIconPath = "";



        // Selected Map Link
        this.m_oSelectedMapLink = null;
        // Selected Sensor Link
        this.m_oSelectedSensorLink = null;
        // Selected Hydro Link
        this.m_oSelectedHydroLink = null;
        // Selected Radar Link
        this.m_oSelectedRadarLink = null;
        // Selected Satellite Link
        this.m_oSelectedSatelliteLink = null;

        // Test of the Geocoding Query
        this.m_sGeocodingQuery = "";
        // Flag to know if a map is active
        this.m_bDynamicLayerActive = false;
        // Flag to Know if a Station Layer is active
        this.m_bSensorLayerActive = false;
        // Flag to know if a map is active
        this.m_bHydroLayerActive = false;
        // Flag to Know if the map legend image is to be shown
        this.m_bShowMapLegendImage = true;
        // Flag to Know if the sensors legend image is to be shown
        this.m_bShowSensorsLegendImage = true;

        // Remembers the actual selected Map third level modifier
        this.m_sMapThirdLevelSelectedModifier = "";
        // Remembers the actual selected Hydro third level modifier
        this.m_sHydroThirdLevelSelectedModifier = "";

        // Map Links Array
        this.m_aoMapLinks = [];
        // Map Third Levels Array
        this.m_aoThirdLevels = [];

        // Sensors Array
        this.m_aoSensorsLinks = [];

        // Statics Array
        this.m_aoStaticLinks = [];

        // Weather Variables
        //this.m_bIsWeatherActive = true;
        this.m_bIsWeatherActive = false;


        // Hydro Links Array
        this.m_aoHydroLinks = [];
        // Hydro Third Levels Array
        this.m_aoHydroThirdLevels = [];


        this.m_aoSatelliteLinks = [];
        this.m_aoRadarLinks = [];


        // Flag to know if the side bar is collapsed or not
        this.m_bSideBarCollapsed = false;

        // default center lat
        this.m_dCenterLat = 8.60;
        // default center lon
        this.m_dCenterLon = 44.20;
        // default zoom
        this.m_iCenterZoom = 9;

        // Flag to know if show hydro section
        this.m_bShowHydro = false;

        // Flag to know if static layer are received
        this.m_bStaticLayersReceived = false;
        // Flag to know if stations are received
        this.m_bStationsReceived = false;
        // Flag to know if the map is ready
        this.m_bMapReady = false;

        this.m_oStopTimerPromise = {};

        this.m_bShowRadar = false;
        this.m_sRadarLegendSelected = "";
        this.m_sRadarLegendHover = "";
        this.m_bIsRadarFirstLevel = true;
        this.m_iRadarLevel = 1;
        this.m_bRadarLayerActive = false;

        this.m_bShowSatellite = false;
        this.m_sSatelliteLegendSelected = "";
        this.m_sSatelliteLegendHover = "";
        this.m_bIsSatelliteFirstLevel = true;
        this.m_iSatelliteLevel = 1;
        this.m_bSatelliteLayerActive = false;


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

        if( typeof( window.innerHeight ) == 'number' ) {
            //Non-IE
            mapHeight = window.innerHeight;
        } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
            //IE 6+ in 'standards compliant mode'
            mapHeight = document.documentElement.clientHeight;
        } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
            //IE 4 compatible
            mapHeight = document.body.clientHeight;
        }



        $("#contentcontainer").height(mapHeight);

        if ($("#omirlMap") != null) {
            $("#omirlMap").height(mapHeight);
        }

        var oControllerVar = this;

        this.m_oMapNavigatorService.fetchMapFirstLevels();

        this.m_oMapNavigatorService.fetchHydroFirstLevels();

        this.m_oMapNavigatorService.fetchRadarFirstLevels();

        this.m_oMapNavigatorService.fetchSatelliteFirstLevels();

        this.m_oMapNavigatorService.getSensorFirstLevel().success(function (data, status) {

            oControllerVar.m_oConstantsService.clearSensorLinks();

            for (var iElement = 0; iElement < data.length; iElement++) {
                oControllerVar.m_aoSensorsLinks.push(data[iElement]);
                oControllerVar.m_oConstantsService.pushToSensorLinks(data[iElement]);
            }

            oControllerVar.m_bStationsReceived = true;
            oControllerVar.FireInitEvent(oControllerVar);

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });


        this.m_oMapNavigatorService.getStaticLayerLinks().success(function (data, status) {

            oControllerVar.m_oConstantsService.clearStaticLinks();

            for (var iElement = 0; iElement < data.length; iElement++) {
                oControllerVar.m_aoStaticLinks.push(data[iElement]);
                oControllerVar.m_oConstantsService.pushToStaticLinks(data[iElement]);
            }

            oControllerVar.m_bStaticLayersReceived = true;
            oControllerVar.FireInitEvent(oControllerVar);
        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Static Layers to add to the Menu');
        });


        // Add Auto Refresh Interval Callback
        this.m_oStopTimerPromise = this.m_oInterval(function() {
                if (oControllerVar.m_oSelectedSensorLink != null) {
                    oControllerVar.showStationsLayer(oControllerVar.m_oSelectedSensorLink);
                }
            },
            this.m_oConstantsService.getRefreshRateMs());


        // Add map ready callback
        this.m_oMapService.callbackArg = this;
        this.m_oMapService.readyCallback = this.MapReadyCallback;


        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oControllerVar.m_oMapService.map != null) {
                oControllerVar.m_oLayerService.clarAll();
                oControllerVar.m_oMapService.map.destroy();
                oControllerVar.m_oMapService.map = null;
                oControllerVar.m_oMapService.stationsPopupControllerAdded = false;
            }

            oControllerVar.m_oInterval.cancel(oControllerVar.m_oStopTimerPromise);
        });

        this.m_oScope.$on('mapInitComplete', function (event, next, current) {
            // Check if there is a user logged
            var oUser = oControllerVar.m_oConstantsService.getUser();
            if (angular.isDefined(oUser))
            {
                if (oUser != null)
                {
                    // Check Map Center
                    var dLat = oUser.defaultLat;
                    var dLon = oUser.defaultLon;
                    var iZoom = oUser.defaultZoom;

                    // Is defined
                    if (angular.isDefined(dLat) && angular.isDefined(dLon) && angular.isDefined(iZoom))
                    {
                        // And not null?
                        if (dLat != null && dLon != null && iZoom != null)
                        {
                            // Ok Let set center informations
                            var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection

                            oControllerVar.m_dCenterLat = dLat;
                            oControllerVar.m_dCenterLon = dLon;
                            oControllerVar.m_iCenterZoom = iZoom;

                            oControllerVar.resetZoom(oControllerVar.m_oMapService.map, dLat, dLon, epsg4326, iZoom);
                        }
                    }


                    // Check Default Sensor View
                    var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(oUser.defaultSensorType);

                    if (oSensorLink != null)
                    {
                        oControllerVar.sensorLinkClicked(oSensorLink);
                    }

                    if (oControllerVar.m_oConstantsService.getUser().defaultStatics != null)
                    {
                        var sStatics = oControllerVar.m_oConstantsService.getUser().defaultStatics;
                        var asStaticLayers = sStatics.split(";");

                        for (var iLayers = 0; iLayers<asStaticLayers.length; iLayers++)
                        {
                            var oStaticLink = oControllerVar.m_oConstantsService.getStaticLinkById(asStaticLayers[iLayers]);

                            if (oStaticLink!=null)
                            {
                                oControllerVar.staticLayerClicked(oStaticLink);
                            }

                        }
                    }

                    // Visibility "test"
                    oControllerVar.m_bShowHydro = true;
                    oControllerVar.m_bShowRadar = true;
                    oControllerVar.m_bShowSatellite = true;

                }// End User != null
            }
        });
    }

    /**
     * Method that fires initMap Event when all needed data are received
     * @param oMapController
     * @constructor
     */
    MapController.prototype.FireInitEvent = function(oMapController) {
        if (oMapController.m_bStaticLayersReceived && oMapController.m_bStationsReceived && oMapController.m_bMapReady)
        {
            oMapController.m_oScope.$broadcast('mapInitComplete');
        }
    }

    MapController.prototype.setBaseLayer = function(sCode) {
        var oBaseLayer = this.m_oMapService.map.getLayersByName(sCode)[0];
        this.m_oMapService.map.setBaseLayer(oBaseLayer);
    }

    MapController.prototype.MapReadyCallback = function(oMapController) {
        //oMapController.AddWeatherLayer(oMapController);

        oMapController.m_bMapReady = true;
        oMapController.FireInitEvent(oMapController);

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
                                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
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
                oControllerVar.m_oLog.error('Error Contacting Omirl Server');
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
                        oControllerVar.m_oLog.error('Error Contacting Omirl Server');
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
            this.m_oSelectedSensorLink = oSensorLink;
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

            this.m_oSelectedSensorLink = null;
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

        //console.log("Now  = " + oDate);
        //console.log("Ref  = " + oReferenceDate);

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

    MapController.prototype.getFeatureOpacity = function(oReferenceDate)
    {
        // Get Now
        var oDate = new Date();

        //console.log("Now  = " + oDate);
        //console.log("Ref  = " + oReferenceDate);

        // Compute time difference
        var iDifference = oDate.getTime()-oReferenceDate.getTime();

        // How it is in minutes?
        var iMinutes = 1000*60;

        var iDeltaMinutes = Math.round(iDifference/iMinutes);

        var iMaxDelayMinutes = 4320;

        if (iDeltaMinutes>iMaxDelayMinutes) return -1.0;

        var dDelayRatio = iDeltaMinutes/iMaxDelayMinutes;

        dDelayRatio = 1.0-dDelayRatio;

        dDelayRatio *= 0.9;

        return dDelayRatio;
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
        var oReferenceDate = new Date(oFeature.attributes.referenceDate+"Z");

        // Compute time delta
        //var sTimeDelta = this.getDelayString(oReferenceDate);
        var sTimeDelta = "";

        var iMonth = oReferenceDate.getMonth() + 1;
        //var iMonth = oReferenceDate.getUTCMonth() + 1;

        var sMinutes = "" + oReferenceDate.getMinutes();
        //var sMinutes = "" + oReferenceDate.getUTCMinutes();

        if (sMinutes.length<2) {
            sMinutes = "0" + sMinutes;
        }

        // Write reference date text
        var sReferenceData = oReferenceDate.getDate() + "/" + iMonth + "/" + oReferenceDate.getFullYear() + " - " + oReferenceDate.getHours() + ":" + sMinutes + " Locali";
        var sReferenceDataUTC = oReferenceDate.getUTCHours() + ":" + sMinutes + " UTC";

        // Start Pop up HTML
        var sHtml = "<div class='stationsPopupPanel'>";

        // Stations Informations
        sHtml += "<strong style=\"font-size: 15px;\">"+oFeature.attributes.municipality+"</strong><br>";

        // Stations Informations
        sHtml += "<p><strong>" + oFeature.attributes.name + " [" + oFeature.attributes.altitude + "  s.l.m.]" + "</strong></p>";

        var iFixedCount  = 1;

        if (oFeature.attributes.sensorType == "Idro") iFixedCount = 2;
        var sValue = parseFloat(oFeature.attributes.value ).toFixed(iFixedCount);

        // Sensor Value
        sHtml +="<h4>"+"<img class='stationsPopupImage' src='"+oFeature.attributes.imageLinkInv+"' style=\"font-family: 'Glyphicons Halflings';font-size: 16px;border: 1px solid #ffffff;padding: 2px;border-radius: 3px;\"/> " + sValue + " " + oFeature.attributes.measureUnit + "</h4>";

        // Time reference
        sHtml += "<p><span class='popupglyphicon glyphicon glyphicon-time' style=\"font-family: 'Glyphicons Halflings';font-size: 15px;\"></span> <span style=\"font-size: 14px;\">" + sTimeDelta + " " + sReferenceData + "</span></p>";

        // Station Code
        sHtml += "<br><div>Codice: " + oFeature.attributes.shortCode + " - ["+sReferenceDataUTC + "] </div>";
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

        if (this.m_oSelectedSensorLink.isClickable==false) return;

        var oControllerVar = this;
        var sStationCode = oFeature.attributes.shortCode;
        var sMunicipality = oFeature.attributes.municipality;
        var sName = oFeature.attributes.name;

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

        var bIsStock = true;

        var bIsStockChart = true;
        bIsStockChart = this.m_oChartService.isStockChart(sSensorType);


        // The data for the dialog
        var model = {
                "stationCode": sStationCode,
                "chartType": sSensorType,
                "isStock": bIsStockChart,
                "municipality": sMunicipality,
                "name": sName
            };


        // jQuery UI dialog options
        var options = {
            autoOpen: false,
            modal: false,
            width: 600,
            resizable: false,
            close: function(event, ui) {
                // Remove the chart from the Chart Service
                oControllerVar.m_oChartService.removeChart(sStationCode);
            },
            title:  oFeature.attributes.name + " (Comune di " + oFeature.attributes.municipality + ")"
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

                while( oServiceVar.m_oMapService.map.popups.length ) {
                    oServiceVar.m_oMapService.map.removePopup(oServiceVar.m_oMapService.map.popups[0]);
                }

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

                var oReferenceDate = new Date(oStation.refDate+"Z");
                var dOpacity = oServiceVar.getFeatureOpacity(oReferenceDate);

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
                    imgPath: oStation.imgPath,
                    // Opacity
                    opacity: dOpacity
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
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
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

        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        this.resetZoom(this.m_oMapService.map,this.m_dCenterLat, this.m_dCenterLon, epsg4326, this.m_iCenterZoom);
    }

    MapController.prototype.resetZoom = function (oMap, dLat, dLon, oFrom, iZoom) {

        // Transformations objects
        var projectTo = oMap.getProjectionObject(); //The map projection (Spherical Mercator)

        //var oProjection = 'EPSG:4326';
        var oCenter = new OpenLayers.LonLat(dLat,dLon).transform(oFrom, projectTo);
        oMap.setCenter(oCenter, iZoom);
    }


    /**
     * Method called when an Hydro link is clicked
     * @param oHydroLink
     * @constructor
     */
    MapController.prototype.HydroLinkClicked = function(oHydroLink)
    {
        // Hydro Link = null stands for back: impossible to have back on first level
        if (this.m_bIsHydroFirstLevel && oHydroLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = this;

        // Is this a Map Link Click?
        if (oHydroLink != null) {
            // Write on screen the Selected Layer description
            if (!this.m_bHydroLayerActive)
            {
                this.m_sHydroLastLegendSelected = this.m_sHydroLegendSelected;
                this.m_sHydroLegendSelected = oHydroLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oHydroLink.selected;
        }
        else {
            if (!this.m_bHydroLayerActive) {
                if (this.m_iHydroLevel == 2) {
                    this.m_sHydroLegendSelected = "Modelli";
                }
                else {
                    this.m_sHydroLegendSelected = this.m_sHydroLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        this.m_aoHydroLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });

        // We are in the first level?
        if (this.m_bIsHydroFirstLevel) {

            if (oHydroLink.hasChilds) {
                // Remember we are in second level
                this.m_bIsHydroFirstLevel = false;

                this.m_iHydroLevel = 2;

                // Clear variables
                oControllerVar.m_aoHydroLinks = [];

                // Get second level from server
                this.m_oMapNavigatorService.getHydroSecondLevels(oHydroLink.linkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoHydroLinks = data;


                    // Is there any Map selected?
                    if (oControllerVar.m_oSelectedHydroLink != null) {

                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoHydroLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoHydroLinks[iCount].linkCode == oControllerVar.m_oSelectedHydroLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedHydroLinkOnScreen(oControllerVar, oControllerVar.m_aoHydroLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else {
                this.switchHydroLinkState(bIsSelected, oHydroLink);
            }
        }
        else if (this.m_iHydroLevel==2) {
            // We are in second level
            if (oHydroLink == null) {
                // Back: get first levels
                this.m_aoHydroLinks = this.m_oMapNavigatorService.getHydroFirstLevels();
                this.m_bIsHydroFirstLevel = true;
                this.m_iHydroLevel= 1;
            }
            else {
                // Switch to show or not third level
                if (oHydroLink.hasThirdLevel) {
                    this.m_iHydroLevel= 3;

                    var oHydroLinkCopy = oHydroLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    this.m_oMapNavigatorService.getHydroThirdLevel(oHydroLink.linkCode).success(function(data,status) {

                        // Third Level Icons
                        oControllerVar.m_aoHydroLinks = data;


                        // Is there any Map selected?
                        if (oControllerVar.m_oSelectedHydroLink != null) {

                            //Is One of these links the one selected?
                            var iCount;
                            for (iCount = 0; iCount< oControllerVar.m_aoHydroLinks.length; iCount++) {

                                // Check by Layer Id
                                if (oControllerVar.m_aoHydroLinks[iCount].linkCode == oControllerVar.m_oSelectedHydroLink.linkCode) {

                                    // This is the selected one!!
                                    oControllerVar.setSelectedHydroLinkOnScreen(oControllerVar, oControllerVar.m_aoHydroLinks[iCount]);

                                    break;
                                }
                            }
                        }

                    }).error(function(data,status){
                        oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                    });

                }
                else {
                    this.switchHydroLinkState(bIsSelected, oHydroLink);
                }
            }
        }
        else if (this.m_iHydroLevel==3)
        {
            // We are in third level
            if (oHydroLink == null)
            {
                // Back: get second levels
                this.m_oMapNavigatorService.getHydroSecondLevels(this.m_aoHydroLinks[0].parentLinkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoHydroLinks = data;
                    oControllerVar.m_bIsHydroFirstLevel = false;
                    oControllerVar.m_iHydroLevel= 2;

                    if (!oControllerVar.m_bHydroLayerActive)
                    {
                        oControllerVar.m_sHydroLegendSelected = data[0].parentDescription;
                    }

                    if (oControllerVar.m_oSelectedHydroLink!=null)
                    {
                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoHydroLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoHydroLinks[iCount].linkCode == oControllerVar.m_oSelectedHydroLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedHydroLinkOnScreen(oControllerVar, oControllerVar.m_aoHydroLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else
            {
                this.switchHydroLinkState(bIsSelected, oHydroLink);
            }
        }
    }

    MapController.prototype.switchHydroLinkState = function(bIsSelected, oHydroLink) {
        if (!bIsSelected)
        {

            this.setSelectedHydroLinkOnScreen(this,oHydroLink);
            //this.selectedDynamicLayer(oHydroLink, this.m_sHydroThirdLevelSelectedModifier);
            alert('attivo ' + oHydroLink.description);
        }
        else
        {

            // Remove from the map

            //if (this.m_oLayerService.getDynamicLayer() != null) {
            //    this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
            //    this.m_oLayerService.setDynamicLayer(null);
            //}

            alert('DISattivo ' + oHydroLink.description);

            this.m_sHydroLegendSelected = oHydroLink.parentDescription;
            this.m_bHydroLayerActive = false;
            this.m_sHydroLegendPath = "";
            this.m_sHydroLegendTooltip = "Legenda Idro";
            this.m_sHydroLegendIconPath = "";

            this.m_oSelectedHydroLink = null;
        }
    }


    MapController.prototype.getHydroLinks = function()
    {
        if (this.m_bIsHydroFirstLevel) {
            this.m_aoHydroLinks = this.m_oMapNavigatorService.getHydroFirstLevels();
        }

        return this.m_aoHydroLinks;
    }


    MapController.prototype.getRadarLinks = function()
    {
        if (this.m_bIsRadarFirstLevel) {
            this.m_aoRadarLinks = this.m_oMapNavigatorService.getRadarFirstLevels();
        }

        return this.m_aoRadarLinks;
    }


    MapController.prototype.getSatelliteLinks = function()
    {
        if (this.m_bIsSatelliteFirstLevel) {
            this.m_aoSatelliteLinks = this.m_oMapNavigatorService.getSatelliteFirstLevels();
        }

        return this.m_aoSatelliteLinks;
    }

    /**
     * Sets all needed variables to show selected Map Link on screen
     * @param oControllerVar
     * @param oMapLink
     */
    MapController.prototype.setSelectedHydroLinkOnScreen = function (oControllerVar, oHydroLink) {

        oHydroLink.selected = true;
        // Layer Click
        oControllerVar.m_sHydroLegendIconPath = oHydroLink.link;
        oControllerVar.m_sHydroLegendTooltip = "Legenda " + oHydroLink.description;
        oControllerVar.m_sHydroLegendSelected = oHydroLink.description;
        oControllerVar.m_bHydroLayerActive = true;
        oControllerVar.m_sHydroLegendPath = oHydroLink.legendLink;
        oControllerVar.m_oSelectedHydroLink = oHydroLink;
    }



    /**
     * Method called when an Radar link is clicked
     * @param oRadarLink
     * @constructor
     */
    MapController.prototype.RadarLinkClicked = function(oRadarLink)
    {
        // Hydro Link = null stands for back: impossible to have back on first level
        if (this.m_bIsRadarFirstLevel && oRadarLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = this;

        // Is this a Map Link Click?
        if (oRadarLink != null) {
            // Write on screen the Selected Layer description
            if (!this.m_bRadarLayerActive)
            {
                this.m_sRadarLastLegendSelected = this.m_sRadarLegendSelected;
                this.m_sRadarLegendSelected = oRadarLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oRadarLink.selected;
        }
        else {
            if (!this.m_bRadarLayerActive) {
                if (this.m_iRadarLevel == 2) {
                    this.m_sRadarLegendSelected = "";
                }
                else {
                    this.m_sRadarLegendSelected = this.m_sRadarLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        this.m_aoRadarLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });

        // We are in the first level?
        if (this.m_bIsRadarFirstLevel) {

            if (oRadarLink.hasChilds) {
                // Remember we are in second level
                this.m_bIsRadarFirstLevel = false;

                this.m_iRadarLevel = 2;

                // Clear variables
                oControllerVar.m_aoRadarLinks = [];

                // Get second level from server
                this.m_oMapNavigatorService.getRadarSecondLevels(oRadarLink.linkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoRadarLinks = data;


                    // Is there any Map selected?
                    if (oControllerVar.m_oSelectedRadarLink != null) {

                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoRadarLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoRadarLinks[iCount].linkCode == oControllerVar.m_oSelectedRadarLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedRadarLinkOnScreen(oControllerVar, oControllerVar.m_aoRadarLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else {
                this.switchRadarLinkState(bIsSelected, oRadarLink);
            }
        }
        else if (this.m_iRadarLevel==2) {
            // We are in second level
            if (oRadarLink == null) {
                // Back: get first levels
                this.m_aoRadarLinks = this.m_oMapNavigatorService.getRadarFirstLevels();
                this.m_bIsRadarFirstLevel = true;
                this.m_iRadarLevel = 1;
            }
            else {
                // Switch to show or not third level
                if (oRadarLink.hasThirdLevel) {
                    this.m_iRadarLevel= 3;

                    var oRadarLinkCopy = oRadarLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    this.m_oMapNavigatorService.getRadarThirdLevel(oRadarLink.linkCode).success(function(data,status) {

                        // Third Level Icons
                        oControllerVar.m_aoRadarLinks = data;


                        // Is there any Map selected?
                        if (oControllerVar.m_oSelectedRadarLink != null) {

                            //Is One of these links the one selected?
                            var iCount;
                            for (iCount = 0; iCount< oControllerVar.m_aoRadarLinks.length; iCount++) {

                                // Check by Layer Id
                                if (oControllerVar.m_aoRadarLinks[iCount].linkCode == oControllerVar.m_oSelectedRadarLink.linkCode) {

                                    // This is the selected one!!
                                    oControllerVar.setSelectedRadarLinkOnScreen(oControllerVar, oControllerVar.m_aoRadarLinks[iCount]);

                                    break;
                                }
                            }
                        }

                    }).error(function(data,status){
                        oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                    });

                }
                else {
                    this.switchRadarLinkState(bIsSelected, oRadarLink);
                }
            }
        }
        else if (this.m_iRadarLevel==3)
        {
            // We are in third level
            if (oRadarLink == null)
            {
                // Back: get second levels
                this.m_oMapNavigatorService.getRadarSecondLevels(this.m_aoRadarLinks[0].parentLinkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoRadarLinks = data;
                    oControllerVar.m_bIsRadarFirstLevel = false;
                    oControllerVar.m_iRadarLevel= 2;

                    if (!oControllerVar.m_bRadarLayerActive)
                    {
                        oControllerVar.m_sRadarLegendSelected = data[0].parentDescription;
                    }

                    if (oControllerVar.m_oSelectedRadarLink!=null)
                    {
                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoRadarLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoRadarLinks[iCount].linkCode == oControllerVar.m_oSelectedRadarLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedRadarLinkOnScreen(oControllerVar, oControllerVar.m_aoRadarLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else
            {
                this.switchRadarLinkState(bIsSelected, oRadarLink);
            }
        }
    }


    MapController.prototype.switchRadarLinkState = function(bIsSelected, oRadarLink) {
        if (!bIsSelected)
        {

            this.setSelectedRadarLinkOnScreen(this,oRadarLink);
            //this.selectedDynamicLayer(oRadarLink, this.m_sHydroThirdLevelSelectedModifier);
            alert('attivo ' + oRadarLink.description);
        }
        else
        {

            // Remove from the map

            //if (this.m_oLayerService.getDynamicLayer() != null) {
            //    this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
            //    this.m_oLayerService.setDynamicLayer(null);
            //}

            alert('DISattivo ' + oRadarLink.description);

            this.m_sRadarLegendSelected = oRadarLink.parentDescription;
            this.m_bRadarLayerActive = false;
            this.m_sRadarLegendPath = "";
            this.m_sRadarLegendTooltip = "";
            this.m_sRadarLegendIconPath = "";

            this.m_oSelectedRadarLink = null;
        }
    }

    /**
     * Sets all needed variables to show selected Radar Link on screen
     * @param oControllerVar
     * @param oMapLink
     */
    MapController.prototype.setSelectedRadarLinkOnScreen = function (oControllerVar, oRadarLink) {

        oRadarLink.selected = true;
        // Layer Click
        oControllerVar.m_sRadarLegendIconPath = oRadarLink.link;
        oControllerVar.m_sRadarLegendTooltip = "Legenda " + oRadarLink.description;
        oControllerVar.m_sRadarLegendSelected = oRadarLink.description;
        oControllerVar.m_bRadarLayerActive = true;
        oControllerVar.m_sRadarLegendPath = oRadarLink.legendLink;
        oControllerVar.m_oSelectedRadarLink = oRadarLink;
    }



























    /**
     * Method called when an Satellite link is clicked
     * @param oSatelliteLink
     * @constructor
     */
    MapController.prototype.SatelliteLinkClicked = function(oSatelliteLink)
    {
        // Hydro Link = null stands for back: impossible to have back on first level
        if (this.m_bIsSatelliteFirstLevel && oSatelliteLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = this;

        // Is this a Map Link Click?
        if (oSatelliteLink != null) {
            // Write on screen the Selected Layer description
            if (!this.m_bSatelliteLayerActive)
            {
                this.m_sSatelliteLastLegendSelected = this.m_sSatelliteLegendSelected;
                this.m_sSatelliteLegendSelected = oSatelliteLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oSatelliteLink.selected;
        }
        else {
            if (!this.m_bSatelliteLayerActive) {
                if (this.m_iSatelliteLevel == 2) {
                    this.m_sSatelliteLegendSelected = "";
                }
                else {
                    this.m_sSatelliteLegendSelected = this.m_sSatelliteLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        this.m_aoSatelliteLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });

        // We are in the first level?
        if (this.m_bIsSatelliteFirstLevel) {

            if (oSatelliteLink.hasChilds) {
                // Remember we are in second level
                this.m_bIsSatelliteFirstLevel = false;

                this.m_iSatelliteLevel = 2;

                // Clear variables
                oControllerVar.m_aoSatelliteLinks = [];

                // Get second level from server
                this.m_oMapNavigatorService.getSatelliteSecondLevels(oSatelliteLink.linkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoSatelliteLinks = data;


                    // Is there any Map selected?
                    if (oControllerVar.m_oSelectedSatelliteLink != null) {

                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoSatelliteLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoSatelliteLinks[iCount].linkCode == oControllerVar.m_oSelectedSatelliteLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedSatelliteLinkOnScreen(oControllerVar, oControllerVar.m_aoSatelliteLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else {
                this.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
            }
        }
        else if (this.m_iSatelliteLevel==2) {
            // We are in second level
            if (oSatelliteLink == null) {
                // Back: get first levels
                this.m_aoSatelliteLinks = this.m_oMapNavigatorService.getSatelliteFirstLevels();
                this.m_bIsSatelliteFirstLevel = true;
                this.m_iSatelliteLevel = 1;
            }
            else {
                // Switch to show or not third level
                if (oSatelliteLink.hasThirdLevel) {
                    this.m_iSatelliteLevel= 3;

                    var oSatelliteLinkCopy = oSatelliteLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    this.m_oMapNavigatorService.getSatelliteThirdLevel(oSatelliteLink.linkCode).success(function(data,status) {

                        // Third Level Icons
                        oControllerVar.m_aoSatelliteLinks = data;


                        // Is there any Map selected?
                        if (oControllerVar.m_oSelectedSatelliteLink != null) {

                            //Is One of these links the one selected?
                            var iCount;
                            for (iCount = 0; iCount< oControllerVar.m_aoSatelliteLinks.length; iCount++) {

                                // Check by Layer Id
                                if (oControllerVar.m_aoSatelliteLinks[iCount].linkCode == oControllerVar.m_oSelectedSatelliteLink.linkCode) {

                                    // This is the selected one!!
                                    oControllerVar.setSelectedSatelliteLinkOnScreen(oControllerVar, oControllerVar.m_aoSatelliteLinks[iCount]);

                                    break;
                                }
                            }
                        }

                    }).error(function(data,status){
                        oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                    });

                }
                else {
                    this.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
                }
            }
        }
        else if (this.m_iSatelliteLevel==3)
        {
            // We are in third level
            if (oSatelliteLink == null)
            {
                // Back: get second levels
                this.m_oMapNavigatorService.getSatelliteSecondLevels(this.m_aoSatelliteLinks[0].parentLinkCode).success(function(data,status) {

                    // Second Level Icons
                    oControllerVar.m_aoSatelliteLinks = data;
                    oControllerVar.m_bIsSatelliteFirstLevel = false;
                    oControllerVar.m_iSatelliteLevel= 2;

                    if (!oControllerVar.m_bSatelliteLayerActive)
                    {
                        oControllerVar.m_sSatelliteLegendSelected = data[0].parentDescription;
                    }

                    if (oControllerVar.m_oSelectedSatelliteLink!=null)
                    {
                        //Is One of these links the one selected?
                        var iCount;
                        for (iCount = 0; iCount< oControllerVar.m_aoSatelliteLinks.length; iCount++) {

                            // Check by Layer Id
                            if (oControllerVar.m_aoSatelliteLinks[iCount].linkCode == oControllerVar.m_oSelectedSatelliteLink.linkCode) {

                                // This is the selected one!!
                                oControllerVar.setSelectedSatelliteLinkOnScreen(oControllerVar, oControllerVar.m_aoSatelliteLinks[iCount]);

                                break;
                            }
                        }
                    }

                }).error(function(data,status){
                    oControllerVar.m_oLog.error('Error Contacting Omirl Server');
                });
            }
            else
            {
                this.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
            }
        }
    }


    MapController.prototype.switchSatelliteLinkState = function(bIsSelected, oSatelliteLink) {
        if (!bIsSelected)
        {

            this.setSelectedSatelliteLinkOnScreen(this,oSatelliteLink);
            //this.selectedDynamicLayer(oRadarLink, this.m_sHydroThirdLevelSelectedModifier);
            alert('attivo ' + oSatelliteLink.description);
        }
        else
        {

            // Remove from the map

            //if (this.m_oLayerService.getDynamicLayer() != null) {
            //    this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
            //    this.m_oLayerService.setDynamicLayer(null);
            //}

            alert('DISattivo ' + oSatelliteLink.description);

            this.m_sSatelliteLegendSelected = oSatelliteLink.parentDescription;
            this.m_bSatelliteLayerActive = false;
            this.m_sSatelliteLegendPath = "";
            this.m_sSatelliteLegendTooltip = "";
            this.m_sSatelliteLegendIconPath = "";

            this.m_oSelectedSatelliteLink = null;
        }
    }

    /**
     * Sets all needed variables to show selected Radar Link on screen
     * @param oControllerVar
     * @param oMapLink
     */
    MapController.prototype.setSelectedSatelliteLinkOnScreen = function (oControllerVar, oSatelliteLink) {

        oSatelliteLink.selected = true;
        // Layer Click
        oControllerVar.m_sSatelliteLegendIconPath = oSatelliteLink.link;
        oControllerVar.m_sSatelliteLegendTooltip = "Legenda " + oSatelliteLink.description;
        oControllerVar.m_sSatelliteLegendSelected = oSatelliteLink.description;
        oControllerVar.m_bSatelliteLayerActive = true;
        oControllerVar.m_sSatelliteLegendPath = oSatelliteLink.legendLink;
        oControllerVar.m_oSelectedSatelliteLink = oSatelliteLink;
    }

    MapController.prototype.tablesLinkClicked = function() {
        this.m_oLocation.path("/stationstable");
    }

    MapController.$inject = [
        '$scope',
        '$window',
        'az.services.layersService',
        'az.services.mapService',
        'MapNavigatorService',
        'StationsService',
        'dialogService',
        'ChartService',
        'ConstantsService',
        '$interval',
        '$log',
        '$location'
    ];

    return MapController;
})();