/**
 * Created by p.campanella on 20/01/14.
 */
'use strict';

/* Controllers */

var MapController = (function () {

    function MapController($scope, $rootScope, $window, layerService, mapService, oMapNavigatorService, oStationsService, oDialogService, oChartService, oConstantsService, $interval, $log, $location, oTableService, oHydroService, oMapLayerService, $translate) {
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
        this.m_oTableService = oTableService;
        this.m_oHydroService = oHydroService;
        this.m_oMapLayerService= oMapLayerService;
        this.m_oTranslateService = $translate;
        // Flag to know if maps first level is shown
        this.m_bIsFirstLevel = true;
        // Flag to know if hydro first level is shown
        this.m_bIsHydroFirstLevel = true;
        this.m_iHydroLevel = 1;
        
        
        //**********************************************************************
        //* Variable and methods used with the menu directive
        //**********************************************************************
        this.MENU_SENSORS = "menu-sensors";
        this.MENU_MAPS = "menu-maps";
        this.MENU_HYDRO = "menu-hydro";
        this.MENU_RADAR = "menu-radar";
        this.MENU_SATELLITE = "menu-satellite";
        this.MENU_LEVEL_1 = 0;
        this.MENU_LEVEL_2 = 1;
        this.MENU_LEVEL_3 = 2;
        
        //this.m_aoSensorsLinksForDirective = null;
        this.m_aoMenuDirectives = {};
        
        this.m_aoMenuLinks = [];
        this.m_aoMenuLinks[this.MENU_SENSORS] = [];
            this.m_aoMenuLinks[this.MENU_SENSORS][this.MENU_LEVEL_1] = [];
            this.m_aoMenuLinks[this.MENU_SENSORS][this.MENU_LEVEL_3] = [];
            this.m_aoMenuLinks[this.MENU_SENSORS][this.MENU_LEVEL_3] = [];
        this.m_aoMenuLinks[this.MENU_MAPS] = [];
            this.m_aoMenuLinks[this.MENU_MAPS][this.MENU_LEVEL_1] = [];
            this.m_aoMenuLinks[this.MENU_MAPS][this.MENU_LEVEL_3] = [];
            this.m_aoMenuLinks[this.MENU_MAPS][this.MENU_LEVEL_3] = [];
        this.m_aoMenuLinks[this.MENU_HYDRO] = [];
            this.m_aoMenuLinks[this.MENU_HYDRO][this.MENU_LEVEL_1] = [];
            this.m_aoMenuLinks[this.MENU_HYDRO][this.MENU_LEVEL_3] = [];
            this.m_aoMenuLinks[this.MENU_HYDRO][this.MENU_LEVEL_3] = [];
        this.m_aoMenuLinks[this.MENU_RADAR] = [];
            this.m_aoMenuLinks[this.MENU_RADAR][this.MENU_LEVEL_1] = [];
            this.m_aoMenuLinks[this.MENU_RADAR][this.MENU_LEVEL_3] = [];
            this.m_aoMenuLinks[this.MENU_RADAR][this.MENU_LEVEL_3] = [];
        this.m_aoMenuLinks[this.MENU_SATELLITE] = [];
            this.m_aoMenuLinks[this.MENU_SATELLITE][this.MENU_LEVEL_1] = [];
            this.m_aoMenuLinks[this.MENU_SATELLITE][this.MENU_LEVEL_3] = [];
            this.m_aoMenuLinks[this.MENU_SATELLITE][this.MENU_LEVEL_3] = [];
        
        // *** Methods ***
        this.onPropertyChanged = function (name, newVal)
        {
            if(newVal)
                $rootScope.$broadcast(name, {newValue : newVal});
            else
                $rootScope.$broadcast(name);
        };
        
        
        //**********************************************************************
        //* Variables listeners
        //**********************************************************************
        // Listeners to watch "menu 1st level links"        
        $scope.$watch(function(){ return $scope.m_oController.m_oMapNavigatorService.getMapFirstLevels(); }, function(newValue){
            console.debug("[MapController] m_oController.m_oMapNavigatorService.getMapFirstLevels", newValue);
            $scope.m_oController.m_aoMapLinks = newValue;
            $scope.m_oController.m_aoMenuLinks[$scope.m_oController.MENU_MAPS][$scope.m_oController.MENU_LEVEL_1] = newValue;
        });
        
        $scope.$watch(function(){ return $scope.m_oController.m_oMapNavigatorService.getHydroFirstLevels(); }, function(newValue){
            console.debug("[MapController] m_oController.m_oMapNavigatorService.getHydroFirstLevels()", newValue);
            $scope.m_oController.m_aoMenuLinks[$scope.m_oController.MENU_HYDRO][$scope.m_oController.MENU_LEVEL_1] = newValue;
        });
        
        $scope.$watch(function(){ return $scope.m_oController.m_oMapNavigatorService.getRadarFirstLevels(); }, function(newValue){
            console.debug("[MapController] m_oController.m_oMapNavigatorService.getRadarFirstLevels()", newValue);
            $scope.m_oController.m_aoMenuLinks[$scope.m_oController.MENU_RADAR][$scope.m_oController.MENU_LEVEL_1] = newValue;
        });
        
        $scope.$watch(function(){ return $scope.m_oController.m_oMapNavigatorService.getSatelliteFirstLevels(); }, function(newValue){
            console.debug("[MapController] m_oController.m_oMapNavigatorService.getSatelliteFirstLevels()", newValue);
            $scope.m_oController.m_aoMenuLinks[$scope.m_oController.MENU_SATELLITE][$scope.m_oController.MENU_LEVEL_1] = newValue;
        });

        //**********************************************************************
        
        

        var oControllerVar = this;

        // Used in HTML

        // Text to be used on the hover for Map legend
        this.m_sMapLegendHover = "";
        // Text to set the selected Map layer
        this.m_sMapLegendSelected = "MAP_T_LEGENDSELECTED";
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
        //Legend prefix
        this.m_sLegendPrefix = "";
        this.m_oTranslateService('MAP_LEGENDTOOLTIP').then(function(text){
            oControllerVar.m_sLegendPrefix = text;
        });


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

        //date and time of selected feature
        this.m_oSelectedSensorDateTimeInfo = "";
        this.m_oSelectedSensorDateTimeIcon = "";
        this.m_oSelectedMapDateTimeInfo = "";
        this.m_oSelectedMapDateTimeIcon = "";


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

        this.m_oReferenceDate = new Date();
        this.m_bNowMode = true;

        this.m_bIsInfoActive = false;


        // Initialize Layer Service
        if (this.m_oLayerService.getBaseLayers().length == 0) {

            // Create Base Layers
            var oBaseLayer1 = new OpenLayers.Layer.Google("Physical", {type: google.maps.MapTypeId.TERRAIN});
            var oBaseLayer2 = new OpenLayers.Layer.Google("Hybrid", {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var oBaseLayer3 = new OpenLayers.Layer.Google("Streets", {numZoomLevels: 20});
            var oBaseLayer4 = new OpenLayers.Layer.Google("Satellite", {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 20});





            // OSM tiles
            var layerBW = new OpenLayers.Layer.XYZ(
                'BW',
                'http://www.toolserver.org/tiles/bw-mapnik/${z}/${x}/${y}.png',
                //'https://a.tile.openstreetmap.org/{z}/{x}/{y}.png',
                {
                    attribution: 'basemap data &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a>',
                    sphericalMercator: true,
                    wrapDateLine: true,
                    transitionEffect: "resize",
                    buffer: 0,
                    numZoomLevels: 20,
                    tileOptions: {crossOriginKeyword: null}
                }
            );
            layerBW.tileOptions.crossOriginKeyword = null;

            var oOSMLayer = new OpenLayers.Layer.OSM();
            oOSMLayer.tileOptions.crossOriginKeyword = null;

            // Add Base Layers
            this.m_oLayerService.addBaseLayer(oOSMLayer);
            this.m_oLayerService.addBaseLayer(oBaseLayer1);
            this.m_oLayerService.addBaseLayer(oBaseLayer2);
            this.m_oLayerService.addBaseLayer(oBaseLayer3);
            this.m_oLayerService.addBaseLayer(oBaseLayer4);
            this.m_oLayerService.addBaseLayer(layerBW);

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


        // Get Reference Date e Now Mode
        this.m_bNowMode = true;

        if (this.m_oConstantsService.getReferenceDate() != null)
        {
            if (this.m_oConstantsService.getReferenceDate() != "")
            {
                this.m_oReferenceDate = this.m_oConstantsService.getReferenceDate();
                this.m_bNowMode = false;
            }
        }

        // Get Map First Levels
        this.m_oMapNavigatorService.fetchMapFirstLevels();

        // Get Hydro Links
        if (this.m_oConstantsService.isUserLogged()) {
            this.m_oMapNavigatorService.fetchHydroFirstLevels();

            this.m_oMapNavigatorService.getFlattedHydro().success(function(data, status) {

                oControllerVar.m_oConstantsService.setFlattedHydroLinks(data);

            }).error(function (data, status) {
                oControllerVar.m_oLog.error('Error Loading Flatted Hydro Links');
            });
        }

        // Get Radar Links
        if (this.m_oConstantsService.isUserLogged()) {
            this.m_oMapNavigatorService.fetchRadarFirstLevels();
        }

        // Get Satellite Links
        if (this.m_oConstantsService.isUserLogged()) {
            this.m_oMapNavigatorService.fetchSatelliteFirstLevels();
        }

        // Get Sesors
        this.m_oMapNavigatorService.getSensorFirstLevel().success(function (data, status) {

            // Clear old result
            oControllerVar.m_oConstantsService.clearSensorLinks();

            //******************************************************************
            // Add the flag to indicate the menu link item level and 
            // if the menu link has a sub-level.
            // or not. These parametere should come from server but, at the
            // moment, are initialized here
            for(var key in data)
            {
                data[key].hasSubLevel = false;
                data[key].myLevel = oControllerVar.MENU_LEVEL_1;
            }
            //******************************************************************



            // Remember links
            for (var iElement = 0; iElement < data.length; iElement++)
            {
                var oSensorLink = data[iElement];
                
                oControllerVar.m_aoSensorsLinks.push(oSensorLink);
                oControllerVar.m_oConstantsService.pushToSensorLinks(oSensorLink);
            }
            oControllerVar.m_bStationsReceived = true;
            
            //oControllerVar.m_aoSensorsLinksForDirective = oControllerVar.m_aoSensorsLinks;
            oControllerVar.m_aoMenuLinks[oControllerVar.MENU_SENSORS][oControllerVar.MENU_LEVEL_1] = oControllerVar.m_aoSensorsLinks;
            
            // Ok we can init the map
            oControllerVar.FireInitEvent(oControllerVar);

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });


        // Get Static Layers
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


        /**
         * Perform the UI alignment of the legend icon and palette
         */
        this.alignStationLegend = function ()
        {
            var oLegendContainer = $("#stations-legend-container");
            var oLegendIcon = $("#stations-legend-container .sensor-legend-icon");
            var oLegendPalette = $("#stations-legend-container .map-legend-image");

            var iLegendContainerHeight = oLegendContainer.height();
            var iPaletteHeight = oLegendPalette.height();
            var iLegendIconHeight = oLegendIcon.height();
            //var iDiff = iLegendContainerHeight - iLegendIconHeight;
            var iDiff = iPaletteHeight - iLegendIconHeight;

            if( iDiff > 0)
            {
                oLegendIcon.css({
                    "margin-top": Math.floor(iDiff / 2)
                });
            }

        }



        // Add Auto Refresh Interval Callback
        this.m_oStopTimerPromise = this.m_oInterval(function() {
                /*
                if (oControllerVar.m_oSelectedSensorLink != null) {
                    oControllerVar.showStationsLayer(oControllerVar.m_oSelectedSensorLink);
                }
                */

                if (oControllerVar.m_oConstantsService.isNowMode())
                {
                    oControllerVar.m_oReferenceDate = new Date();
                }

                oControllerVar.refreshFullMap(oControllerVar);
            },
            this.m_oConstantsService.getRefreshRateMs());


        // Add map ready callback
        this.m_oMapService.callbackArg = this;
        this.m_oMapService.readyCallback = this.MapReadyCallback;

        this.infoControls = {
            click: new OpenLayers.Control.WMSGetFeatureInfo({
                url: this.m_oConstantsService.getWMSURL(),
                title: 'Layer Info',
                queryVisible: true
            })
        };

        // When we are leaving the page....
        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oControllerVar.m_oMapService.map != null) {

                // Remember what is on the screen!
                var oCenter = oControllerVar.m_oMapService.map.getCenter();

                var oZoom = oControllerVar.m_oMapService.map.getZoom();

                oControllerVar.m_oConstantsService.setMapCenter(oCenter);
                oControllerVar.m_oConstantsService.setMapZoom(oZoom);

                oControllerVar.m_oLayerService.clarAll();
                oControllerVar.m_oMapService.map.destroy();
                oControllerVar.m_oMapService.map = null;
                oControllerVar.m_oMapService.stationsPopupControllerAdded = false;
                oControllerVar.m_oMapService.sectionsPopupControllerAdded = false;
            }

            oControllerVar.m_oInterval.cancel(oControllerVar.m_oStopTimerPromise);
        });

        this.m_oScope.$on('mapInitComplete', function (event, next, current)
        {


            if (oControllerVar.m_oConstantsService.getIsMiniVersion()) {
                var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType("Vento");
                oControllerVar.m_oSelectedSensorLink = oSensorLink;
                oControllerVar.sensorLinkClicked(oSensorLink);
                return;
            }

            // Check if there is a user logged
            var oUser = oControllerVar.m_oConstantsService.getUser();
            var bDataSet = false;
            if (angular.isDefined(oUser))
            {
                if (oUser != null)
                {
                    bDataSet = true;

                    // Check Map Center
                    var dLat = oUser.defaultLat;
                    var dLon = oUser.defaultLon;
                    var iZoom = oUser.defaultZoom;

                    // If the user just logged, load defaults
                    if (!oControllerVar.m_oConstantsService.getJustLogged())
                    {
                        // Old Zoom
                        if (oControllerVar.m_oConstantsService.getMapZoom()!=null)
                        {
                            iZoom = oControllerVar.m_oConstantsService.getMapZoom();
                        }

                        // Old Center
                        if (oControllerVar.m_oConstantsService.getMapCenter()!=null)
                        {
                            var oCenter = oControllerVar.m_oConstantsService.getMapCenter();

                            var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
                            var projectTo = oControllerVar.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

                            // Tranform the point
                            var oPoint = new OpenLayers.Geometry.Point(oCenter.lon, oCenter.lat).transform(projectTo, epsg4326);

                            dLat = oPoint.x;
                            dLon = oPoint.y;
                        }

                        // Sensor Layer
                        if (oControllerVar.m_oConstantsService.getSensorLayerActive()!=null)
                        {
                            var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(oControllerVar.m_oConstantsService.getSensorLayerActive());
                            oControllerVar.sensorLinkClicked(oSensorLink);
                        }
                    }
                    else
                    {
                        // Ok clear just logged flag
                        oControllerVar.m_oConstantsService.setJustLogged(false);

                        // Check Default Sensor View
                        var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(oUser.defaultSensorType);

                        if (oSensorLink != null)
                        {
                            oControllerVar.sensorLinkClicked(oSensorLink);
                        }
                    }

                    // Is defined
                    if (angular.isDefined(dLat) && angular.isDefined(dLon) && angular.isDefined(iZoom))
                    {
                        // And not null?
                        if (dLat != null && dLon != null && iZoom != null)
                        {
                            // Ok Let set center informations
                            var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection

                            oControllerVar.resetZoom(oControllerVar.m_oMapService.map, dLat, dLon, epsg4326, iZoom);
                        }
                    }

                    // Any static layer?
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

            if (!bDataSet) {
                // Get Default Center and Zoom
                var dLat = oControllerVar.m_dCenterLat;
                var dLon = oControllerVar.m_dCenterLon;
                var iZoom = oControllerVar.m_iCenterZoom;

                // Check if zoom available in constant service
                if (oControllerVar.m_oConstantsService.getMapZoom()!=null)
                {
                    iZoom = oControllerVar.m_oConstantsService.getMapZoom();
                }

                // Check if center available in constant service
                if (oControllerVar.m_oConstantsService.getMapCenter()!=null)
                {
                    // Get The center
                    var oCenter = oControllerVar.m_oConstantsService.getMapCenter();

                    // Need to convert
                    var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
                    var projectTo = oControllerVar.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

                    // Tranform the point
                    var oPoint = new OpenLayers.Geometry.Point(oCenter.lon, oCenter.lat).transform(projectTo, epsg4326);

                    // Set center lat and lon
                    dLat = oPoint.x;
                    dLon = oPoint.y;
                }

                // Is defined
                if (angular.isDefined(dLat) && angular.isDefined(dLon) && angular.isDefined(iZoom))
                {
                    // And not null?
                    if (dLat != null && dLon != null && iZoom != null)
                    {
                        // Ok Let set center informations
                        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection

                        oControllerVar.resetZoom(oControllerVar.m_oMapService.map, dLat, dLon, epsg4326, iZoom);
                    }
                }

                if (oControllerVar.m_oConstantsService.getSensorLayerActive()!=null)
                {
                    var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(oControllerVar.m_oConstantsService.getSensorLayerActive());
                    oControllerVar.sensorLinkClicked(oSensorLink);
                }
            }

            for (var i in oControllerVar.infoControls)
            {
                oControllerVar.infoControls[i].events.register("beforegetfeatureinfo", oControllerVar, oControllerVar.askInfo);
                oControllerVar.infoControls[i].events.register("getfeatureinfo", oControllerVar, oControllerVar.showInfo);
                oControllerVar.m_oMapService.map.addControl(oControllerVar.infoControls[i]);
            }


        });



    }

    MapController.prototype.alignOpenLayerControllerToMap = function()
    {
        if( this.m_oMapService && this.m_oMapService.map) {
            this.m_oMapService.map.updateSize();
        }
    }

    MapController.prototype.hasSensorLegendLink = function()
    {
        var sLegendPath = this.m_oScope.m_oController.m_sSensorsLegendPath;
        return ( Utils.isStrNullOrEmpty(sLegendPath) == false);
    }



    MapController.prototype.getMapTitle = function()
    {    
        return this.m_sMapLegendSelected
    }

    MapController.prototype.askInfo = function(evt) {
        console.log('ask');

        /*
        var url =  layer_group.getFullRequestString({
            REQUEST: "GetFeatureInfo",
            EXCEPTIONS: "application/vnd.ogc.se_xml",
            BBOX: layer_group.map.getExtent().toBBOX(),
            X: e.xy.x,
            Y: e.xy.y,
            INFO_FORMAT: 'text/html',
            QUERY_LAYERS: layer_group.params.LAYERS,
            WIDTH: layer_group.map.size.w,
            HEIGHT: layer_group.map.size.h});

        window.open(url,
            "getfeatureinfo",
            "location=0,status=0,scrollbars=1,width=600,height=150"
        );
        */
    }

    MapController.prototype.showInfo = function(evt) {
        console.log(evt.text);

        vex.dialog.alert({
            message: evt.text,
        });

        /*
        var oControllerVar = this;
        this.m_oMapService.map.addPopup(new OpenLayers.Popup.Popup.FramedCloud(
            "chicken",
            oControllerVar.m_oMapService.map.getLonLatFromPixel(evt.xy),
            null,
            event.text,
            null,
            true
        ));*/

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

        /*
        Per ora questa è l'unica soluzione trovata per il problema dell'Info Tools
        Un timeout di tre secondi permette alla mappa di essere pronta e comunque l'utente in tre secondi non ha il tempo di lavorare sulla mappa
         */

        setTimeout( function() {
            oMapController.alignOpenLayerControllerToMap();
        }, 3000);

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
        oGeoCoder.geocode( {'address': sGeocodingQuery + " Liguria"}, function(aoResults, oStatus) {

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
     * Method that can be called to obtain the width center
     * @returns {number}
     */
    MapController.prototype.getLegendRight = function() {
        var iWidth = this.m_oWindow.innerWidth;
        return iWidth/2;
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


    ///////////////////////////////////////////////////////  MAPS /////////////////////////////////////////////////////////////////////////////


    /**
     * Click on a Icon of the navigator
     * @param oMapLink Map link or null for back
     */
    MapController.prototype.mapLinkClicked = function (oMapLink, oController)
    {
        //console.debug("Clic on:", oMapLink, " - is 1st level:", oController.m_bIsFirstLevel)
        if( !oController )
            oController = this;

        // Map Link = null stands for back: impossible to have back on first level
        if (oController.m_bIsFirstLevel && oMapLink == null) return;

        // Clear member variables
        oController.m_bShowThirdLevel = false;
        oController.m_aoThirdLevels = [];

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Is this a Map Link Click?
        if (oMapLink != null) {
            // Write on screen the Selected Layer description
            if (!oController.m_bDynamicLayerActive && oController.m_bIsFirstLevel) oController.m_sMapLegendSelected = oMapLink.description;
            // Remember if it was selected or not
            bIsSelected = oMapLink.selected;
        }
        else {
            // No is a Back Click
            if (!oController.m_bDynamicLayerActive) oController.m_sMapLegendSelected = "MAP_T_LEGENDSELECTED";
        }

        // Clear all selection flags
        oController.m_aoMapLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });


        // We are in the first level?
        if (oController.m_bIsFirstLevel) {

            // Remember we are in second level
            oController.m_bIsFirstLevel = false;

            // Clear variables and remember the Controller ref
            var oControllerVar = oController;
            oControllerVar.m_aoMapLinks = [];

            // Get second level from server
            oController.m_oMapNavigatorService.getMapSecondLevels(oMapLink.linkId).success(function(data,status) {

                //******************************************************************
                // Add the flag to indicate the menu link item level and 
                // if the menu link has a sub-level.
                // or not. These parametere should come from server but, at the
                // moment, are initialized here
                for(var key in data)
                {
                    data[key].hasSubLevel = true;
                    data[key].myLevel = oController.MENU_LEVEL_2;
                }
                //******************************************************************
                
                // Second Level Icons
                oControllerVar.m_aoMapLinks = data;
                oControllerVar.m_aoMenuLinks[oControllerVar.MENU_MAPS][oControllerVar.MENU_LEVEL_2] = oControllerVar.m_aoMapLinks;

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
                                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
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
                oControllerVar.m_oLog.error('Error contacting Omirl Server');
            });

        }
        else {
            // We are in second level
            if (oMapLink == null) {
                // Back: get first levels
                oController.m_aoMapLinks = oController.m_oMapNavigatorService.getMapFirstLevels();
                oController.m_bIsFirstLevel = true;
            }
            else {
                // Switch to show or not third level
                if (oMapLink.hasThirdLevel )
                {
                    if ( bIsSelected == false )
                    {
                        // Load third level only if the 2nd level item is not selected
                        var oMapLinkCopy = oMapLink;
                        var oControllerVar = oController;

                        oControllerVar.m_aoThirdLevels = [];

                        // Get third levels from the service
                        oControllerVar.m_oMapNavigatorService.getMapThirdLevel(oMapLink).success(function(data,status) {

                            oControllerVar.gotMapThirdLevelFromServer(data, status,oControllerVar,oMapLinkCopy);
                        }).error(function(data,status){
                            oControllerVar.m_oLog.error('Error contacting Omirl Server');
                        });
                    }

                }
                else {
                    oController.m_sMapThirdLevelSelectedModifier = "";
                }

                if (!bIsSelected) {
                    if (!oMapLink.hasThirdLevel)
                    {
                        oController.setSelectedMapLinkOnScreen(oController,oMapLink);
                        oController.selectedDynamicLayer(oMapLink, oController.m_sMapThirdLevelSelectedModifier, oController);
                    }
                }
                else {
                    // Remove from the map
                    if (oController.m_oLayerService.getDynamicLayer() != null) {
                        oController.m_oMapService.map.removeLayer(oController.m_oLayerService.getDynamicLayer());
                        oController.m_oLayerService.setDynamicLayer(null);

                        oController.m_oSelectedMapDateTimeIcon = "";
                        oController.m_oSelectedMapDateTimeInfo = "";
                    }
                    oController.m_sMapLegendSelected = "";
                    oController.m_bShowThirdLevel = false;
                    oController.m_bDynamicLayerActive = false;
                    oController.m_sMapLegendPath = "";
                    oController.m_sMapLegendTooltip = "Legenda Mappa";
                    oController.m_sMapLegendIconPath = "";

                    oController.m_oSelectedMapLink = null;
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

        // Qui Pulire Direttiva Radar e Satellite
        oControllerVar.m_aoMenuDirectives[oControllerVar.MENU_RADAR].resetDirectiveSelections();
        oControllerVar.m_aoMenuDirectives[oControllerVar.MENU_SATELLITE].resetDirectiveSelections();
        
        oMapLink.selected = true;
        // Layer Click
        oControllerVar.m_sMapLegendIconPath = oMapLink.link;
        this.m_oTranslateService(oMapLink.description).then(function(text){
            oControllerVar.m_sMapLegendTooltip = oControllerVar.m_sLegendPrefix + " " + text;

        });

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
    MapController.prototype.gotMapThirdLevelFromServer = function(data,status, oControllerVar, oMapLinkCopy)
    {
        //******************************************************************
        // Add the flag to indicate the menu link item level and
        // if the menu link has a sub-level.
        // or not. These parametere should come from server but, at the
        // moment, are initialized here
        for(var key in data)
        {
            data[key].hasSubLevel = false;
            data[key].myLevel = oControllerVar.MENU_LEVEL_3;
        }
        //******************************************************************

        oControllerVar.m_aoThirdLevels = data;
        oControllerVar.m_bShowThirdLevel = true;
        oControllerVar.m_aoMenuLinks[oControllerVar.MENU_MAPS][oControllerVar.MENU_LEVEL_3] = data;
        
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
    MapController.prototype.selectedDynamicLayer = function (oMapLink, sModifier, oController)
    {

        if( !oController )
            oController = this;

        if (!angular.isDefined(sModifier)) {
            sModifier = "none";
        }

        if (sModifier == "") {
            sModifier = "none";
        }

        var oController = this;

        //reset date time
        oController.m_oSelectedMapDateTimeInfo = "";
        oController.m_oSelectedMapDateTimeIcon = "";

        var sLayerCode = oMapLink.layerID;
        var asStrings = oMapLink.layerID.split(":");
        if (asStrings != null)
        {
            sLayerCode = asStrings[1];
        }

        var sOldLayerIdentifier = "";

        if (oController.m_oLayerService.getDynamicLayer() != null)
        {
            sOldLayerIdentifier = oController.m_oLayerService.getDynamicLayer().params.LAYERS;
        }

        this.m_oSelectedSatelliteLink = null;
        this.m_oSelectedRadarLink = null;

        oController.m_oMapLayerService.getLayerId(sLayerCode, sModifier).success(function (data, status) {

            oController.setSelectedMapLinkOnScreen(oController,oMapLink);

            if (data.layerId != null && data.layerId != sOldLayerIdentifier) {
                // Create WMS Layer
                var oLayer = new OpenLayers.Layer.WMS(oMapLink.description, oMapLink.layerWMS, {
                    layers: data.layerId,
                    transparent: "true",
                    format: "image/png"
                });
                oLayer.isBaseLayer = false;

                // Remove last one
                if (oController.m_oLayerService.getDynamicLayer() != null) {
                    oController.m_oMapService.map.removeLayer(oController.m_oLayerService.getDynamicLayer());
                }

                oLayer.setOpacity(0.6);
                // Add the new layer to the map
                oController.m_oLayerService.setDynamicLayer(oLayer);
                oController.m_oMapService.map.addLayer(oLayer);
                oController.m_oMapService.map.setLayerIndex(oLayer, oController.m_oLayerService.getBaseLayers().length);

            }
            else if (data.layerId == null)
            {
                // Remove last one
                if (oController.m_oLayerService.getDynamicLayer() != null) {
                    oController.m_oMapService.map.removeLayer(oController.m_oLayerService.getDynamicLayer());
                    oController.m_oLayerService.setDynamicLayer(null);
                }

                oController.m_oTranslateService('MAP_NOT_AVAILABLE').then(function(msg)
                {
                    oController.activeDirectiveScope.callbackDeselectLastClickedMenuItem(oMapLink.myLevel);
                    
                    vex.dialog.alert({
                        message: msg
                    });
                    //alert(msg);
                });
            }

            //set date time
            if (angular.isDefined(data.updateDateTime) && data.updateDateTime != null) {
                var oDate = new Date(data.updateDateTime + " UTC");
                oController.m_oTranslateService('MAP_LAYERDATEINFO', {data: oDate.toString()}).then(function (msg) {
                    oController.m_oSelectedMapDateTimeInfo = msg;
                    oController.m_oSelectedMapDateTimeIcon = oMapLink.link;
                });
            }
        }).error(function (data, status) {
            oController.setSelectedMapLinkOnScreen(oController,oController.m_oSelectedMapLink);
            oController.m_oTranslateService('ERRORCONTACTSERVER').then(function(error){
                vex.dialog.alert({
                        message: error,
                    });
                //alert(error);
            });
        });
    }

    /**
     * Function called when a Dynamic Layer is set
     * @param oMapLink Link to the layer
     * @param sModifier Modifier of the layer Id
     */
    MapController.prototype.selectedRadarSatDynamicLayer = function (oMapLink, sModifier) {

        if (!angular.isDefined(sModifier)) {
            sModifier = "none";
        }

        if (sModifier == "") {
            sModifier = "none";
        }

        var oController = this;

        var sLayerCode = oMapLink.linkCode;

        if (oMapLink.linkCode.indexOf(":")>-1)
        {
            var asStrings = oMapLink.linkCode.split(":");
            if (asStrings != null)
            {
                sLayerCode = asStrings[1];
            }
        }

        var sOldLayerIdentifier = "";

        if (oController.m_oLayerService.getDynamicLayer() != null)
        {
            sOldLayerIdentifier = oController.m_oLayerService.getDynamicLayer().params.LAYERS;
        }

        oController.m_oMapLayerService.getLayerId(sLayerCode, sModifier).success(function (data, status) {

            //oController.setSelectedMapLinkOnScreen(oController,oMapLink);

            if (data.layerId != null && data.layerId != sOldLayerIdentifier) {
                // Create WMS Layer
                var oLayer = new OpenLayers.Layer.WMS(oMapLink.description, oMapLink.layerWMS, {
                    layers: data.layerId,
                    transparent: "true",
                    format: "image/png"
                });
                oLayer.isBaseLayer = false;

                // Remove last one
                if (oController.m_oLayerService.getDynamicLayer() != null) {
                    oController.m_oMapService.map.removeLayer(oController.m_oLayerService.getDynamicLayer());
                }

                oLayer.setOpacity(0.6);
                // Add the new layer to the map
                oController.m_oLayerService.setDynamicLayer(oLayer);
                oController.m_oMapService.map.addLayer(oLayer);
                oController.m_oMapService.map.setLayerIndex(oLayer, oController.m_oLayerService.getBaseLayers().length);

                //set date time
                if (angular.isDefined(data.updateDateTime) && data.updateDateTime != null) {
                    var oDate = new Date(data.updateDateTime + " UTC");
                    oController.m_oTranslateService('MAP_LAYERDATEINFO', {data: oDate.toString()}).then(function (msg) {
                        oController.m_oSelectedMapDateTimeInfo = msg;
                        oController.m_oSelectedMapDateTimeIcon = oMapLink.link;
                    });
                }

            }
            else if (data.layerId == null)
            {
                //reset date time
                oController.m_oSelectedMapDateTimeIcon = "";
                oController.m_oSelectedMapDateTimeInfo = "";

                // Remove last one
                if (oController.m_oLayerService.getDynamicLayer() != null) {
                    oController.m_oMapService.map.removeLayer(oController.m_oLayerService.getDynamicLayer());
                    oController.m_oLayerService.setDynamicLayer(null);
                }

                oController.m_oTranslateService('MAP_NOT_AVAILABLE').then(function(msg){
                    oController.activeDirectiveScope.callbackDeselectLastClickedMenuItem(oMapLink.myLevel);

                    vex.dialog.alert({
                        message: msg
                    });

                });
            }
        }).error(function (data, status) {
            //oController.setSelectedMapLinkOnScreen(oController,oController.m_oSelectedMapLink);
            oController.m_oTranslateService('ERRORCONTACTSERVER').then(function(error)
            {
                vex.dialog.alert({
                        message: error,
                    });
                //alert(error);
            });
        });
    }

    /**
     * Function called when a third level element is clicked
     * @param oThirdLevel
     */
    MapController.prototype.mapThirdLevelClicked = function (oThirdLevel, oController)
    {
        if( !oController )
            oController = this;
        
        // Save actual description
        oController.m_sMapThirdLevelSelected = oThirdLevel.description;
        // Save actual modifier
        oController.m_sMapThirdLevelSelectedModifier = oThirdLevel.layerIDModifier;
        // Show the layer
        oController.selectedDynamicLayer(oThirdLevel.mapItem, oThirdLevel.layerIDModifier, oController);
    }

    MapController.prototype.getMapLinks = function (oController) {
        
        if( !oController)
            oController = this;
            
        if (oController.m_bIsFirstLevel) {
            oController.m_aoMapLinks = oController.m_oMapNavigatorService.getMapFirstLevels();
        }
        return oController.m_aoMapLinks;
    }
    
//    MapController.prototype.getMapLinksForDirective = function ()
//    {        
//        if ($scope.m_oController.m_bIsFirstLevel == true) {
//            this.m_aoMapLinks = this.m_oMapNavigatorService.getMapFirstLevels();
//        }
//        return this.m_aoMapLinks;
//    }

    MapController.prototype.getMapThirdLevels = function() {
        return this.m_aoThirdLevels;
    }


    /**
     * Method called when the user clicks on the map legend Icon: switches show flag of the legend image
     */
    MapController.prototype.mapLegendClicked = function() {
        this.m_bShowMapLegendImage = !this.m_bShowMapLegendImage;
    }

    ////////////////////////////////////////////////// SENSORS ///////////////////////////////////////////////////////////////////////////

    /**
     * Return Sensor Links
     * @returns {Array}
     */
    MapController.prototype.getSensorLinks = function() {
        return this.m_aoSensorsLinks;
    }

    /**
     * Function called when a sensor type is clicked
     * @param oSensorLink
     */
    MapController.prototype.sensorLinkClicked = function (oSensorLink, oController)
    {
        if( !oController )
            oController = this;

        // Check if the sensor link is active
        if (!oSensorLink.isActive){
            // Set the textual description
            oController.m_sSensorLegendSelected = oSensorLink.description;

            // Reset all actives flag
            oController.m_aoSensorsLinks.forEach(function(oEntry) {
                oEntry.isActive = false;
            });

            // Set this as the active one
            oSensorLink.isActive = true;

            //TODO: QUI PULIRE LA DIRETTIVA IDRO
            if( oController.m_aoMenuDirectives[oController.MENU_HYDRO] )
                oController.m_aoMenuDirectives[oController.MENU_HYDRO].resetDirectiveSelections();
            
            oController.hideSectionLayer();

            // Set
            oController.showStationsLayer(oSensorLink);
            oController.m_bSensorLayerActive = true;
            oController.m_sSensorsLegendPath = oSensorLink.legendLink;
            oController.m_sSensorsLegendIconPath = oSensorLink.imageLinkOff;
            oController.m_oTranslateService(oSensorLink.description).then(function(text){
                oController.m_sSensorLegendTooltip = oController.m_sLegendPrefix + " " + text;
            });
            oController.m_oSelectedSensorLink = oSensorLink;

            oController.m_oConstantsService.setSensorLayerActive(oSensorLink.code);

            // Perform some corrections to station legend elements
            setTimeout(oController.alignStationLegend, 200);
        }
        else {
            oController.hideSensorLayer();
        }
        
        if( oController.m_aoMenuDirectives[oController.MENU_SENSORS] )
        {
            oController.m_aoMenuDirectives[oController.MENU_SENSORS].updateByController(0, oSensorLink);
        }
    }

    /**
     * Hide Sensor Layer
     */
    MapController.prototype.hideSensorLayer = function() {
        this.m_oConstantsService.setSensorLayerActive(null);

        // Set the textual description
        this.m_sSensorLegendSelected = "";

        // Reset all actives flag
        this.m_aoSensorsLinks.forEach(function(oEntry) {
            oEntry.isActive = false;
        });

        this.m_sSensorsLegendPath = "";
        try{
            // remove the Sensors Layer from the map
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getSensorsLayer());
        }
        catch (err) {

        }

        this.m_bSensorLayerActive = false;
        this.m_sSensorsLegendIconPath = "";
        this.m_sSensorLegendTooltip = "";

        this.m_oSelectedSensorLink = null;

        this.m_oSelectedSensorDateTimeIcon = "";
        this.m_oSelectedSensorDateTimeInfo = "";

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

    /**
     * Compute opacity starting from Delay
     * @param oReferenceDate    Reference Date
     * @returns {number}        Opacity Percentage (Double 0-1)
     */
    MapController.prototype.getFeatureOpacity = function(oReferenceDate)
    {
        // Get Now
        var oDate = this.m_oConstantsService.getReferenceDate();
        if (oDate == null) oDate = new Date();
        if (oDate == "") oDate = new Date();

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

        var oController = this;
        if (this.m_oSelectedSensorLink.isClickable==false) return;

        if (oFeature.attributes.sensorType == 'webcam') {

            WebcamDialog.add(oFeature);

            return;
            /*
             oController.m_oTranslateService('MAP_MISSINGWEBCAM').then(function(msg)
             {
             vex.dialog.alert({
             message: msg
             });
             //alert(msg);
             return;
             });
             //alert('Missing WebCam Image');
             */
        }

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
                "name": sName,
                "subTitle": ""
            };



        oControllerVar.m_oTranslateService('DIALOGTITLE', {name: oFeature.attributes.name, municipality: oFeature.attributes.municipality, subTitle: ""}).then(function(text){
            // jQuery UI dialog options
            var options = {
                autoOpen: false,
                modal: false,
                width: 'auto',
                resizable: false,
                close: function(event, ui) {
                    // Remove the chart from the Chart Service
                    oControllerVar.m_oChartService.removeChart(sStationCode);
                },
                title: text
            };

            oControllerVar.m_oDialogService.open(sStationCode,"stationsChart.html", model, options)

        });

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

        if (angular.isDefined(oSensorLink.legends)) {
            if (oSensorLink.legends.length>0){
                this.m_oLayerService.addLayerLegend(oSensorLink.code, oSensorLink.legends);
            }
        }


        // Obtain Stations Values from the server
        this.m_oStationsService.getStations(oSensorLink).success(function(data,status) {

            var aoStations = data;

            //update date time info
            oServiceVar.m_oSelectedSensorDateTimeInfo = "";
            oServiceVar.m_oSelectedSensorDateTimeIcon = "";
            if (aoStations != null && aoStations.length > 0)
            {
                if (angular.isDefined(aoStations[0].updateDateTime) && aoStations[0].updateDateTime != null) {
                    var oDate = new Date(aoStations[0].updateDateTime + " UTC");
                    oServiceVar.m_oTranslateService('MAP_STATIONDATEINFO', {data: oDate.toString()}).then(function (msg) {
                        oServiceVar.m_oSelectedSensorDateTimeInfo = msg;
                        oServiceVar.m_oSelectedSensorDateTimeIcon = oSensorLink.imageLinkOff;
                    });
                }
            }

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

            // Get System Reference Date
            var oSystemRefDate = oServiceVar.m_oConstantsService.getReferenceDate();
            if (oSystemRefDate == null) oSystemRefDate = new Date();
            if (oSystemRefDate == "") oSystemRefDate = new Date();

            // For each station
            var iStations;
            var aoFeatures = [];

            aoStations.sort(oServiceVar.compareStations);

            for ( iStations =0; iStations<aoStations.length; iStations++) {
                var oStation = aoStations[iStations];

                // Create the feature
//                var oFeature = new OpenLayers.Feature.Vector(
//                    
//                    {description: oStation.description}
//                    //,{externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
//                    //,{title: oStation.name + " " + oStation.value }
//                );
                //************************************************************
                // To draw a circle
                //************************************************************
                var oMarkerIcon = new OpenLayers.Geometry.Point(oStation.lon, oStation.lat).transform(epsg4326, projectTo);
                

                //************************************************************
                // To draw a X as poly (s = size of X)
                //************************************************************
                //  1) x, y+s
                //  2) x-s, y+2s
                //  3) x-2s, y+s
                //  4) x-s, y
                //  5) x-2s, y-s
                //  6) x-s, y-2s
                //  7) x, y-s
                //  8) x+s, y-2s
                //  9) x+2s, y-s
                //  10) x+s, y
                //  11) x+2s, y+s
                //  12) x+s, y+2s
//                var fSize = 0.004;
//                var aoPoints = [
//                    new OpenLayers.Geometry.Point(oStation.lon, oStation.lat + fSize),              // 1
//                    new OpenLayers.Geometry.Point(oStation.lon - fSize, oStation.lat  + 2*fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon - 2*fSize, oStation.lat  + fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon - fSize, oStation.lat),              // 4
//                    new OpenLayers.Geometry.Point(oStation.lon - 2*fSize, oStation.lat - fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon - fSize, oStation.lat - 2*fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon, oStation.lat - fSize),              // 7
//                    new OpenLayers.Geometry.Point(oStation.lon + fSize, oStation.lat - 2*fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon + 2*fSize, oStation.lat - fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon + fSize, oStation.lat),              // 10
//                    new OpenLayers.Geometry.Point(oStation.lon + 2*fSize, oStation.lat + fSize),
//                    new OpenLayers.Geometry.Point(oStation.lon + fSize, oStation.lat + 2*fSize)
//                ];
//                var oRing = new OpenLayers.Geometry.LinearRing(aoPoints);
//                var oMarkerIcon = new OpenLayers.Geometry.Polygon([oRing.transform(epsg4326, projectTo)]);
                // ************************************************************
                
                
                var oFeature = new OpenLayers.Feature.Vector(
                    oMarkerIcon,
                    {description: oStation.description}
                    //,{externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
                    //,{title: oStation.name + " " + oStation.value }
                );
        

                // Get Increment by server
                var iIncrement = oStation.increment;

                var oReferenceDate = new Date(oStation.refDate+"Z");
                var dOpacity = oServiceVar.getFeatureOpacity(oReferenceDate);

                var lTimeGap = oSystemRefDate.getTime()-oReferenceDate.getTime();
                if (lTimeGap > 7*24*60*60*1000) continue;

                //color
                var dValue = oStation.value;
                if (oSensorLink.code == 'Sfloc'){

                    var oRefDate = new Date(oStation.refDate);

                    dValue = oRefDate.getHours()*60;
                    dValue += oRefDate.getMinutes();
                }

                var oColor = null;
                if (angular.isDefined(oSensorLink.legends)) {
                    for (var iColors = 0; iColors < oSensorLink.legends.length; iColors++) {
                        if (dValue < oSensorLink.legends[iColors].lmt) {
                            oColor = oSensorLink.legends[iColors].clr;
                            break;
                        }
                    }
                }

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
                    opacity: dOpacity,
                    //Color
                    color: oColor
                };

                // Add the feature to the array
                aoFeatures.push(oFeature);
            }

            // Add feature array to the layer
            oServiceVar.m_oLayerService.getSensorsLayer().addFeatures(aoFeatures);

            // Add the layer to the map
            oServiceVar.m_oMapService.map.addLayer(oServiceVar.m_oLayerService.getSensorsLayer());
            oServiceVar.m_oMapService.map.setLayerIndex(oServiceVar.m_oLayerService.getSensorsLayer(), oServiceVar.m_oLayerService.getSensorsLayerIndex());

            //Refresh WebCam
            if (oFeature) {
                if (oFeature.attributes.sensorType == 'webcam') {
                    WebcamDialog.refreshWebcamImage(oFeature.attributes.shortCode, oFeature.attributes.imgPath);
                }
            }

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

            if (aoStations.length == 0)
            {

                // If the layer is 'Fulminazioni (Sfloc)' change the alert message
                // into "Nessuna fulminazione registrata"
                if( oSensorLink.code == "Sfloc")
                {
                    oServiceVar.m_oTranslateService('MAP_NOT_AVAILABLE_SFLOC').then(function(msg){
                        oServiceVar.activeDirectiveScope.callbackDeselectLastClickedMenuItem(oSensorLink.myLevel);

                        vex.dialog.alert({
                            message: msg
                        });

                    });
                }

                oServiceVar.m_oTranslateService('MAP_NOT_AVAILABLE').then(function(msg){
                    oServiceVar.activeDirectiveScope.callbackDeselectLastClickedMenuItem(oSensorLink.myLevel);

                    vex.dialog.alert({
                        message: msg
                    });

                });
            }

        }).error(function(data,status){
            oServiceVar.m_oLog.error('Error contacting Omirl Server');
        });
    }


    /**
     *  Method called when the user clicks on the sensor legend Icon: switches show flag of the legend image
     */
    MapController.prototype.sensorsLegendClicked = function() {
        this.m_bShowSensorsLegendImage = !this.m_bShowSensorsLegendImage;
    }


    //////////////////////////////////////////////////////////////// STATICS /////////////////////////////////////////////////////////////

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


    MapController.prototype.getStaticLinks = function() {
        return this.m_aoStaticLinks;
    }




    ////////////////////////////////////////////////////// WEATHER //////////////////////////////////////////////////////


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

    //////////////////////////////////////////////INFO ////////////////////////////////////////////////////////////////
    MapController.prototype.ToggleInfoTool = function() {

        /*
        this.m_oMapService.map.events.register('click',  this.m_oMapService.map, function (e) {
            var url =  layer_group.getFullRequestString({
                REQUEST: "GetFeatureInfo",
                EXCEPTIONS: "application/vnd.ogc.se_xml",
                BBOX: layer_group.map.getExtent().toBBOX(),
                X: e.xy.x,
                Y: e.xy.y,
                INFO_FORMAT: 'text/html',
                QUERY_LAYERS: layer_group.params.LAYERS,
                WIDTH: layer_group.map.size.w,
                HEIGHT: layer_group.map.size.h});

            window.open(url,
                "getfeatureinfo",
                "location=0,status=0,scrollbars=1,width=600,height=150"
            );
        });
        */




        if  (this.m_bIsInfoActive)  {
            this.m_bIsInfoActive = false;
            this.infoControls.click.deactivate();
        }
        else {
            this.m_bIsInfoActive = true;
            this.infoControls.click.activate();
        }

    }

    //////////////////////////////////////////////HYDRO LINKS//////////////////////////////////////////////////////////
    /**
     * Return the array of Hydro Links
     * @returns {Array}
     */
    MapController.prototype.getHydroLinks = function()
    {
        debugger;
        if (this.m_bIsHydroFirstLevel)
        {
            this.m_aoHydroLinks = this.m_oMapNavigatorService.getHydroFirstLevels();
            this.m_aoMenuLinks[this.MENU_HYDRO][this.MENU_LEVEL_1] = this.m_aoHydroLinks;
        }

        return this.m_aoHydroLinks;
    }


    /**
     * Method called when an Hydro link is clicked
     * @param oHydroLink
     * @constructor
     */
    MapController.prototype.HydroLinkClicked = function(oHydroLink, oController)
    {
        if( !oController )
            oController = this;
        
        // Hydro Link = null stands for back: impossible to have back on first level
        if (oController.m_bIsHydroFirstLevel && oHydroLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = oController;

        // Is this a Map Link Click?
        if (oHydroLink != null) {
            // Write on screen the Selected Layer description
            if (!oController.m_bHydroLayerActive)
            {
                oController.m_sHydroLastLegendSelected = oController.m_sHydroLegendSelected;
                oController.m_sHydroLegendSelected = oHydroLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oHydroLink.selected;
        }
        else {
            if (!oController.m_bHydroLayerActive) {
                if (oController.m_iHydroLevel == 2) {
                    oController.m_sHydroLegendSelected = "Modelli";
                }
                else {
                    oController.m_sHydroLegendSelected = oController.m_sHydroLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        oController.m_aoHydroLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });

        if (oHydroLink.myLevel==0) {
            oController.m_bIsHydroFirstLevel = true;
        }
        // We are in the first level?
        if (oController.m_bIsHydroFirstLevel) {
        //if (oHydroLink.myLevel==1) {

            if (oHydroLink.hasChilds) {
                // Remember we are in second level
                oController.m_bIsHydroFirstLevel = false;

                oController.m_iHydroLevel = 2;

                // Clear variables
                oControllerVar.m_aoHydroLinks = [];

                // Get second level from server
                oController.m_oMapNavigatorService.getHydroSecondLevels(oHydroLink.linkCode).success(function(data,status) {

                    //******************************************************************
                    // Add the flag to indicate the menu link item level and 
                    // if the menu link has a sub-level.
                    // or not. These parametere should come from server but, at the
                    // moment, are initialized here
                    for(var key in data)
                    {
                        data[key].hasSubLevel = true;
                        data[key].myLevel = 2;
                    }
                    //******************************************************************
                    
                    // Second Level Icons
                    oControllerVar.m_aoHydroLinks = data;
                    oController.m_aoMenuLinks[oController.MENU_HYDRO][oController.MENU_LEVEL_2] = data;


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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else {
                oController.switchHydroLinkState(bIsSelected, oHydroLink);
            }
        }
        else if (oController.m_iHydroLevel==2) {
            // We are in second level
            if (oHydroLink == null) {
                // Back: get first levels
                oController.m_aoHydroLinks = oController.m_oMapNavigatorService.getHydroFirstLevels();
                oController.m_bIsHydroFirstLevel = true;
                oController.m_iHydroLevel= 1;
            }
            else {
                // Switch to show or not third level
                if (oHydroLink.hasThirdLevel) {
                    oController.m_iHydroLevel= 3;

                    var oHydroLinkCopy = oHydroLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    oController.m_oMapNavigatorService.getHydroThirdLevel(oHydroLink.linkCode).success(function(data,status) {

                        // Third Level Icons
                        //******************************************************************
                        // Add the flag to indicate the menu link item level and 
                        // if the menu link has a sub-level.
                        // or not. These parametere should come from server but, at the
                        // moment, are initialized here
                        for(var key in data)
                        {
                            data[key].hasSubLevel = false;
                            data[key].myLevel = 3;
                        }
                        //******************************************************************
                        oControllerVar.m_aoHydroLinks = data;
                        oController.m_aoMenuLinks[oController.MENU_HYDRO][oController.MENU_LEVEL_3] = data;


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
                        oControllerVar.m_oLog.error('Error contacting Omirl Server');
                    });

                }
                else {
                    oController.switchHydroLinkState(bIsSelected, oHydroLink);
                }
            }
        }
        else if (oController.m_iHydroLevel==3)
        {
            // We are in third level
            if (oHydroLink == null)
            {
                // Back: get second levels
                oController.m_oMapNavigatorService.getHydroSecondLevels(oController.m_aoHydroLinks[0].parentLinkCode).success(function(data,status) {

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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else
            {
                oController.switchHydroLinkState(bIsSelected, oHydroLink);
            }
        }
    }


    /**
     * Called to show or hide one Hydro Link
     * @param bIsSelected
     * @param oHydroLink
     */
    MapController.prototype.switchHydroLinkState = function(bIsSelected, oHydroLink) {

        if (!bIsSelected)
        {
            //TODO: QUI PULIRE LA DIRETTIVA STAZIONI
            if( this.m_aoMenuDirectives[this.MENU_SENSORS] )
            {
                this.m_aoMenuDirectives[this.MENU_SENSORS].resetDirectiveSelections();
            }
            
            this.hideSensorLayer();
            this.setSelectedHydroLinkOnScreen(this,oHydroLink);
            this.showSectionsLayer(oHydroLink);
        }
        else
        {
            // Remove from the map
            this.hideSectionLayer();
        }
    }

    /**
     * Gets the HTML content of the pop up
     * @param oFeature
     * @returns {string}
     */
    MapController.prototype.getSectionPopupContent = function(oFeature) {

        // Start Pop up HTML
        var sHtml = "<div class='stationsPopupPanel'>";

        // Stations Informations
        sHtml += "<strong style=\"font-size: 15px;\">"+oFeature.attributes.name+"</strong><br>";

        // Stations Informations
        sHtml += "<p><strong>" + oFeature.attributes.basin + " [" + oFeature.attributes.river + "]" + "</strong></p>";

        //var iFixedCount  = 1;

        //if (oFeature.attributes.sensorType == "Idro") iFixedCount = 2;
        //var sValue = parseFloat(oFeature.attributes.value ).toFixed(iFixedCount);

        // Sensor Value
        sHtml +="<h4>"+"<img class='stationsPopupImage' src='"+oFeature.attributes.imageLinkInv+"' style=\"font-family: 'Glyphicons Halflings';font-size: 16px;border: 1px solid #ffffff;padding: 2px;border-radius: 3px;\"/> Area: " + oFeature.attributes.warningArea + "</h4>";

        // Time reference
        sHtml += "<p><span style=\"font-size: 14px;\"> " + oFeature.attributes.basinClass + " ( " + oFeature.attributes.basinArea + " Km<sup>2</sup> )</span></p>";

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
     * Function called to show the selected Sections layer
     * @param oSensorLink
     */
    MapController.prototype.showSectionsLayer = function(oSectionLink) {

        var oControllerVar = this;

        // RESET DATE INFO
        oControllerVar.m_oSelectedSensorDateTimeInfo = "";
        oControllerVar.m_oSelectedSensorDateTimeIcon = "";
        // Obtain Stations Values from the server
        this.m_oHydroService.getSections(oSectionLink).success(function(data,status) {

            var aoSections = data;

            if (data != null) {

                if (data.length>0)
                {
                    //SET DATE INFO
                    if (angular.isDefined(data[0].updateDateTime) && data[0].updateDateTime != null) {
                        var oDate = new Date(data[0].updateDateTime + " UTC");
                        oControllerVar.m_oTranslateService('MAP_SECTIONDATEINFO', {data: oDate.toString()}).then(function (msg) {
                            oControllerVar.m_oSelectedSensorDateTimeInfo = msg;
                            oControllerVar.m_oSelectedSensorDateTimeIcon = oSectionLink.imageLinkOff;
                        });
                    }
                }
            }

            try{

                while( oControllerVar.m_oMapService.map.popups.length ) {
                    oControllerVar.m_oMapService.map.removePopup(oControllerVar.m_oMapService.map.popups[0]);
                }

                // remove the actual Sensors Layer from the map
                oControllerVar.m_oMapService.map.removeLayer(oControllerVar.m_oLayerService.getSectionsLayer());

            }
            catch (err) {

            }

            // Clear the layer
            oControllerVar.m_oLayerService.getSectionsLayer().destroyFeatures();

            // Projection change for points
            var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
            var projectTo = oControllerVar.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

            // For each station
            var iSections;
            var aoFeatures = [];

            aoSections.sort(oControllerVar.compareStations);

            for ( iSections =0; iSections<aoSections.length; iSections++) {
                var oSection = aoSections[iSections];

                // Create the feature
                var oFeature = new OpenLayers.Feature.Vector(
                    new OpenLayers.Geometry.Point(oSection.lon, oSection.lat).transform(epsg4326, projectTo),
                    {description: oSection.name}
                    //,{externalGraphic: oStation.imgPath, graphicHeight: 32, graphicWidth: 32, graphicXOffset:0, graphicYOffset:0, title: oStation.name + " " + oStation.value }
                    //,{title: oStation.name + " " + oStation.value }
                );

                if (angular.isUndefined(oSection.color)) oSection.color=-1;

                // Set attributes of the Feature
                oFeature.attributes = {
                    // Station Id
                    stationId: oSection.code,
                    // Station Name
                    name: oSection.name,
                    // Other Html content for the pop up, received from the server
                    otherHtml: oSection.otherHtml,
                    // Altitude
                    altitude: oSection.alt,
                    // Coordinates
                    lat: oSection.lat,
                    lon: oSection.lon,
                    // Station Code
                    shortCode: oSection.code,
                    // Image Link to use in the popup
                    imageLinkInv: oSectionLink.link,
                    // Sensor Type
                    model: oSection.model,
                    // Municipality
                    municipality: oSection.municipality,
                    // Image Path
                    imgPath: oSection.link,
                    // Opacity
                    opacity: 1.0,
                    //Color
                    color: oSection.color,
                    basin: oSection.basin,
                    river: oSection.river,
                    basinArea: oSection.basinArea,
                    warningArea: oSection.warningArea,
                    basinClass: oSection.basinClass
                };

                // Add the feature to the array
                aoFeatures.push(oFeature);
            }

            // Add feature array to the layer
            oControllerVar.m_oLayerService.getSectionsLayer().addFeatures(aoFeatures);

            // Add the layer to the map
            oControllerVar.m_oMapService.map.addLayer(oControllerVar.m_oLayerService.getSectionsLayer());
            oControllerVar.m_oMapService.map.setLayerIndex(oControllerVar.m_oLayerService.getSectionsLayer(), oControllerVar.m_oLayerService.getSectionsLayerIndex());

            // Feature Click and Hover Control: added?
            if (oControllerVar.m_oMapService.sectionsPopupControllerAdded == false) {

                // No: take a reference to the map controller
                var oMapController = oControllerVar;

                // Create the Control
                var oPopupCtrl = new OpenLayers.Control.SelectFeature(oControllerVar.m_oLayerService.getSectionsLayer(), {
                    hover: true,
                    onSelect: function(feature) {
                        // Hover Select: call internal function
                        oMapController.showSectionsPopup(feature);
                    },
                    onUnselect: function(feature) {
                        // Hover Unselect: remove pop up
                        feature.layer.map.removePopup(feature.popup);
                    },
                    callbacks: {
                        // Click
                        click: function(feature) {
                            if (feature.attributes.color != -1) {
                                // Show chart
                                oMapController.showSectionChart(feature);
                            }
                        }
                    }
                });

                // Add and activate the control
                oControllerVar.m_oMapService.map.addControl(oPopupCtrl);
                oPopupCtrl.activate();

                // Remember it exists now
                oControllerVar.m_oMapService.sectionsPopupControllerAdded = true;
            }
        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error contacting Omirl Server');
        });

    }


    /**
     * Hide sections layer
     */
    MapController.prototype.hideSectionLayer = function(oHydroLink) {

        var oController = this;

        try{
            // remove the Sections Layer from the map
            this.m_oMapService.map.removeLayer(this.m_oLayerService.getSectionsLayer());
        }
        catch (err) {

        }

        // Clear descriptions and flag
        if (oHydroLink != null) {
            this.m_sHydroLegendSelected = oHydroLink.parentDescription;
        }
        else {
            // Clear all selection flags
            this.m_aoHydroLinks.forEach(function(oEntry) {
                oEntry.selected = false;
            });

            this.m_sHydroLegendSelected = "";
        }

        this.m_bHydroLayerActive = false;
        this.m_sHydroLegendPath = "";
        if (angular.isDefined(oHydroLink)) {
            this.m_oTranslateService(oHydroLink.description).then(function (text) {
                oController.m_sHydroLegendTooltip = oController.m_sLegendPrefix + " " + text;
            });
        }
        else
        {
            this.m_oTranslateService('MAP_IDROLEGENDTOOLTIP').then(function (text) {
                oController.m_sHydroLegendTooltip = text;
            });
        }
        //this.m_sHydroLegendTooltip = "Legenda Idro";
        this.m_sHydroLegendIconPath = "";

        this.m_oSelectedHydroLink = null;

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
        this.m_oTranslateService(oHydroLink.description).then(function(text){
            oControllerVar.m_sHydroLegendTooltip = oControllerVar.m_sLegendPrefix + " " + text;
        });
        //oControllerVar.m_sHydroLegendTooltip = "Legenda " + oHydroLink.description;
        oControllerVar.m_sHydroLegendSelected = oHydroLink.description;
        oControllerVar.m_bHydroLayerActive = true;
        oControllerVar.m_sHydroLegendPath = oHydroLink.legendLink;
        oControllerVar.m_oSelectedHydroLink = oHydroLink;
    }


    /**
     * Function called to show station pop up
     * @param oFeature
     */
    MapController.prototype.showSectionsPopup = function(oFeature) {

        // Get Pop Up HTML
        var sHtml = this.getSectionPopupContent(oFeature);
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

    MapController.prototype.showSectionChart = function(oFeature) {

        var oControllerVar = this;
        var sSectionCode = oFeature.attributes.stationId;
        var sBasin = oFeature.attributes.basin;
        var sName = oFeature.attributes.name;

        if (this.m_oDialogService.isExistingDialog(sSectionCode)) {
            return;
        }

        var sModel = this.m_oSelectedHydroLink.linkCode;


        // The data for the dialog
        var model = {
            "sectionCode": sSectionCode,
            "chartType": sModel,
            "basin": sBasin,
            "name": sName
        };


        // jQuery UI dialog options
        var options = {
            autoOpen: false,
            modal: false,
            resizable: false,
            close: function(event, ui) {
                // Remove the chart from the Chart Service
                oControllerVar.m_oChartService.removeChart(sSectionCode);
            },
            title:  oFeature.attributes.name + " - " + sBasin + "",
            position: {my: "left top", at: "left top"},
            width: 'auto',
            height: 600,
            dialogClass:'sectionChartDialog'
        };

        this.m_oDialogService.open(sSectionCode,"sectionChart.html", model, options)
    }



    //////////////////////////////////////////////////////RADAR////////////////////////////////////////////////////////


    /**
     * Gets Radar Links Array
     * @returns {Array}
     */
    MapController.prototype.getRadarLinks = function()
    {
        if (this.m_bIsRadarFirstLevel) {
            this.m_aoRadarLinks = this.m_oMapNavigatorService.getRadarFirstLevels();
        }

        return this.m_aoRadarLinks;
    }

    /**
     * Method called when an Radar link is clicked
     * @param oRadarLink
     * @constructor
     */
    MapController.prototype.RadarLinkClicked = function(oRadarLink, oController)
    {
        if( !oController )
            oController = this;        
        
        // Hydro Link = null stands for back: impossible to have back on first level
        if (oController.m_bIsRadarFirstLevel && oRadarLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = oController;

        // Is this a Map Link Click?
        if (oRadarLink != null) {
            // Write on screen the Selected Layer description
            if (!oController.m_bRadarLayerActive)
            {
                oController.m_sRadarLastLegendSelected = oController.m_sRadarLegendSelected;
                oController.m_sRadarLegendSelected = oRadarLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oRadarLink.selected;
        }
        else {
            if (!oController.m_bRadarLayerActive) {
                if (oController.m_iRadarLevel == 2) {
                    oController.m_sRadarLegendSelected = "";
                }
                else {
                    oController.m_sRadarLegendSelected = oController.m_sRadarLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        oController.m_aoRadarLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });



        // We are in the first level?
        if (oController.m_bIsRadarFirstLevel)
        {
            oController.m_aoMenuLinks[oController.MENU_RADAR][oController.MENU_LEVEL_1].forEach(function(oEntry)
            {
                if( oEntry.description != oRadarLink.description)
                    oEntry.selected = false;
            });

            if (oRadarLink.hasChilds) {
                // Remember we are in second level
                oController.m_bIsRadarFirstLevel = false;

                oController.m_iRadarLevel = 2;

                // Clear variables
                oControllerVar.m_aoRadarLinks = [];

                // Get second level from server
                oController.m_oMapNavigatorService.getRadarSecondLevels(oRadarLink.linkCode).success(function(data,status) {
                    
                    //******************************************************************
                    // Add the flag to indicate the menu link item level and 
                    // if the menu link has a sub-level.
                    // or not. These parametere should come from server but, at the
                    // moment, are initialized here
                    for(var key in data)
                    {
                        data[key].hasSubLevel = data[key].hasChilds;
                        data[key].myLevel = 2;
                    }
                    //******************************************************************
                    
                    // Second Level Icons
                    oControllerVar.m_aoRadarLinks = data;
                    oControllerVar.m_aoMenuLinks[oControllerVar.MENU_RADAR][oControllerVar.MENU_LEVEL_2] = data;


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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else {
                oController.switchRadarLinkState(bIsSelected, oRadarLink);
            }
        }
        else if (oController.m_iRadarLevel==2) {
            // We are in second level
            if (oRadarLink == null) {
                // Back: get first levels
                oController.m_aoRadarLinks = oController.m_oMapNavigatorService.getRadarFirstLevels();
                oController.m_bIsRadarFirstLevel = true;
                oController.m_iRadarLevel = 1;
            }
            else {
                // Switch to show or not third level
                if (oRadarLink.hasThirdLevel) {
                    oController.m_iRadarLevel= 3;

                    var oRadarLinkCopy = oRadarLink;
                    var oControllerVar = this;

                    // Get third levels from the service
                    oController.m_oMapNavigatorService.getRadarThirdLevel(oRadarLink.linkCode).success(function(data,status) {

                        //******************************************************************
                        // Add the flag to indicate the menu link item level and 
                        // if the menu link has a sub-level.
                        // or not. These parametere should come from server but, at the
                        // moment, are initialized here
                        for(var key in data)
                        {
                            data[key].hasSubLevel = data[key].hasChilds;
                            data[key].myLevel = 3;
                        }
                        //******************************************************************
                        //
                        // Third Level Icons
                        oControllerVar.m_aoRadarLinks = data;
                        oControllerVar.m_aoMenuLinks[oControllerVar.MENU_RADAR][oControllerVar.MENU_LEVEL_3] = data;


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
                        oControllerVar.m_oLog.error('Error contacting Omirl Server');
                    });

                }
                else {
                    oController.switchRadarLinkState(bIsSelected, oRadarLink);
                }
            }
        }
        else if (oController.m_iRadarLevel==3)
        {
            // We are in third level
            if (oRadarLink == null)
            {
                // Back: get second levels
                oController.m_oMapNavigatorService.getRadarSecondLevels(oController.m_aoRadarLinks[0].parentLinkCode).success(function(data,status) {

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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else
            {
                oController.switchRadarLinkState(bIsSelected, oRadarLink);
            }
        }
    }


    MapController.prototype.switchRadarLinkState = function(bIsSelected, oRadarLink) {

        if (!bIsSelected)
        {
            // TODO: Qui pulire direttiva Mappe e Satelliti
            if( this.m_aoMenuDirectives[this.MENU_MAPS] && this.m_aoMenuDirectives[this.MENU_SATELLITE])
            {
                this.m_aoMenuDirectives[this.MENU_MAPS].resetDirectiveSelections();
                this.m_aoMenuDirectives[this.MENU_SATELLITE].resetDirectiveSelections();
            }

            this.m_oSelectedMapLink = null;
            this.m_oSelectedSatelliteLink = null;
            
            this.setSelectedRadarLinkOnScreen(this,oRadarLink);
            this.selectedRadarSatDynamicLayer(oRadarLink, "none");
            //alert('attivo ' + oRadarLink.description);
        }
        else
        {

            // Remove from the map
            oRadarLink.selected = false;
            if (this.m_oLayerService.getDynamicLayer() != null) {
                this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
                this.m_oLayerService.setDynamicLayer(null);
            }

            //alert('DISattivo ' + oRadarLink.description);

            this.m_sRadarLegendSelected = oRadarLink.parentDescription;
            this.m_bRadarLayerActive = false;
            this.m_sRadarLegendPath = "";
            this.m_sRadarLegendTooltip = "";
            this.m_sRadarLegendIconPath = "";

            this.m_oSelectedRadarLink = null;

            this.m_oSelectedMapDateTimeInfo = "";
            this.m_oSelectedMapDateTimeIcon = "";
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
        this.m_oTranslateService(oRadarLink.description).then(function(text){
            oControllerVar.m_sRadarLegendTooltip = oControllerVar.m_sLegendPrefix + " " + text;
        });
        //oControllerVar.m_sRadarLegendTooltip = "Legenda " + oRadarLink.description;
        oControllerVar.m_sRadarLegendSelected = oRadarLink.description;
        oControllerVar.m_bRadarLayerActive = true;
        oControllerVar.m_sRadarLegendPath = oRadarLink.legendLink;
        oControllerVar.m_oSelectedRadarLink = oRadarLink;
    }


    //////////////////////////////////////////////////////////////////////SATELLITE/////////////////////////////////////


    /**
     * Gets Satellite Links Array
     * @returns {Array}
     */
    MapController.prototype.getSatelliteLinks = function()
    {
        if (this.m_bIsSatelliteFirstLevel) {
            this.m_aoSatelliteLinks = this.m_oMapNavigatorService.getSatelliteFirstLevels();
        }

        return this.m_aoSatelliteLinks;
    }


    /**
     * Method called when an Satellite link is clicked
     * @param oSatelliteLink
     * @constructor
     */
    MapController.prototype.SatelliteLinkClicked = function(oSatelliteLink, oController)
    {
        if( !oController )
            oController = oController;
            
        // Hydro Link = null stands for back: impossible to have back on first level
        if (oController.m_bIsSatelliteFirstLevel && oSatelliteLink == null) return;

        // var to know if this is the selected entry
        var bIsSelected = false;

        // Remember Controller ref
        var oControllerVar = oController;

        // Is this a Map Link Click?
        if (oSatelliteLink != null) {
            // Write on screen the Selected Layer description
            if (!oController.m_bSatelliteLayerActive)
            {
                oController.m_sSatelliteLastLegendSelected = oController.m_sSatelliteLegendSelected;
                oController.m_sSatelliteLegendSelected = oSatelliteLink.description;
            }
            // Remember if it was selected or not
            bIsSelected = oSatelliteLink.selected;
        }
        else {
            if (!oController.m_bSatelliteLayerActive) {
                if (oController.m_iSatelliteLevel == 2) {
                    oController.m_sSatelliteLegendSelected = "";
                }
                else {
                    oController.m_sSatelliteLegendSelected = oController.m_sSatelliteLastLegendSelected;
                }
            }
        }

        // Clear all selection flags
        oController.m_aoSatelliteLinks.forEach(function(oEntry) {
            oEntry.selected = false;
        });

        // We are in the first level?
        if (oController.m_bIsSatelliteFirstLevel)
        {

            oController.m_aoMenuLinks[oController.MENU_SATELLITE][oController.MENU_LEVEL_1].forEach(function(oEntry)
            {
                if( oEntry.description != oSatelliteLink.description)
                    oEntry.selected = false;
            });

            if (oSatelliteLink.hasChilds) {
                // Remember we are in second level
                oController.m_bIsSatelliteFirstLevel = false;

                oController.m_iSatelliteLevel = 2;

                // Clear variables
                oControllerVar.m_aoSatelliteLinks = [];

                // Get second level from server
                oController.m_oMapNavigatorService.getSatelliteSecondLevels(oSatelliteLink.linkCode).success(function(data,status) {

                    //******************************************************************
                    // Add the flag to indicate the menu link item level and 
                    // if the menu link has a sub-level.
                    // or not. These parametere should come from server but, at the
                    // moment, are initialized here
                    for(var key in data)
                    {
                        debugger;
                        data[key].hasSubLevel = data[key].hasChilds;
                        data[key].myLevel = 1;
                    }
                    //******************************************************************
                    
                    // Second Level Icons
                    oControllerVar.m_aoSatelliteLinks = data;
                    oControllerVar.m_aoMenuLinks[oControllerVar.MENU_SATELLITE][oControllerVar.MENU_LEVEL_2] = data;


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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else {
                oController.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
            }
        }
        else if (oController.m_iSatelliteLevel==2) {
            // We are in second level
            if (oSatelliteLink == null) {
                // Back: get first levels
                oController.m_aoSatelliteLinks = oController.m_oMapNavigatorService.getSatelliteFirstLevels();
                oController.m_bIsSatelliteFirstLevel = true;
                oController.m_iSatelliteLevel = 1;
            }
            else {
                // Switch to show or not third level
                if (oSatelliteLink.hasThirdLevel) {
                    oController.m_iSatelliteLevel= 3;

                    var oSatelliteLinkCopy = oSatelliteLink;
                    var oControllerVar = oController;

                    // Get third levels from the service
                    oController.m_oMapNavigatorService.getSatelliteThirdLevel(oSatelliteLink.linkCode).success(function(data,status) {

                        //******************************************************************
                        // Add the flag to indicate the menu link item level and 
                        // if the menu link has a sub-level.
                        // or not. These parametere should come from server but, at the
                        // moment, are initialized here
                        for(var key in data)
                        {
                            debugger;
                            data[key].hasSubLevel = data[key].hasChilds;
                            data[key].myLevel = 1;
                        }
                        //******************************************************************
                        
                        // Third Level Icons
                        oControllerVar.m_aoSatelliteLinks = data;
                        oControllerVar.m_aoMenuLinks[oControllerVar.MENU_SATELLITE][oControllerVar.MENU_LEVEL_3] = data;


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
                        oControllerVar.m_oLog.error('Error contacting Omirl Server');
                    });

                }
                else {
                    oController.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
                }
            }
        }
        else if (oController.m_iSatelliteLevel==3)
        {
            // We are in third level
            if (oSatelliteLink == null)
            {
                // Back: get second levels
                oController.m_oMapNavigatorService.getSatelliteSecondLevels(oController.m_aoSatelliteLinks[0].parentLinkCode).success(function(data,status) {

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
                    oControllerVar.m_oLog.error('Error contacting Omirl Server');
                });
            }
            else
            {
                oController.switchSatelliteLinkState(bIsSelected, oSatelliteLink);
            }
        }
    }


    /**
     * Called to show or hyde a Satellite link
     * @param bIsSelected
     * @param oSatelliteLink
     */
    MapController.prototype.switchSatelliteLinkState = function(bIsSelected, oSatelliteLink) {
        if (!bIsSelected)
        {
            // TODO: Qui pulire direttiva Mappe e Radar
            if(this.m_aoMenuDirectives[this.MENU_MAPS] && this.m_aoMenuDirectives[this.MENU_RADAR])
            {
                this.m_aoMenuDirectives[this.MENU_MAPS].resetDirectiveSelections();
                this.m_aoMenuDirectives[this.MENU_RADAR].resetDirectiveSelections();
            }

            this.m_oSelectedMapLink = null;
            this.m_oSelectedRadarLink = null;
            
            this.setSelectedSatelliteLinkOnScreen(this,oSatelliteLink);
            this.selectedRadarSatDynamicLayer(oSatelliteLink, "none");
            //alert('attivo ' + oSatelliteLink.description);
        }
        else
        {

            // Remove from the map
            oSatelliteLink.selected = false;
            if (this.m_oLayerService.getDynamicLayer() != null) {
                this.m_oMapService.map.removeLayer(this.m_oLayerService.getDynamicLayer());
                this.m_oLayerService.setDynamicLayer(null);
            }

            //alert('DISattivo ' + oSatelliteLink.description);

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
        this.m_oTranslateService(oSatelliteLink.description).then(function(text){
            oControllerVar.m_sSatelliteLegendTooltip = oControllerVar.m_sLegendPrefix + " " + text;
        });
        //oControllerVar.m_sSatelliteLegendTooltip = "Legenda " + oSatelliteLink.description;
        oControllerVar.m_sSatelliteLegendSelected = oSatelliteLink.description;
        oControllerVar.m_bSatelliteLayerActive = true;
        oControllerVar.m_sSatelliteLegendPath = oSatelliteLink.legendLink;
        oControllerVar.m_oSelectedSatelliteLink = oSatelliteLink;
    }






    MapController.prototype.tablesLinkClicked = function(sPath) {
        this.m_oTableService.dataTableLinkClickedByLink(sPath);
        this.m_oLocation.path(sPath);
    }

    MapController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.refreshFullMap(this);
        this.m_bNowMode = false;
        //console.log(newDate);
        //console.log(oldDate);
    }


    MapController.prototype.refreshFullMap = function (oController) {
        // Selected Map Link
        if (oController.m_oSelectedMapLink != null)
        {
            oController.selectedDynamicLayer(oController.m_oSelectedMapLink,oController.m_sMapThirdLevelSelectedModifier);
        }

        // Selected Sensor Link
        if (oController.m_oSelectedSensorLink != null)
        {
            oController.showStationsLayer(oController.m_oSelectedSensorLink);
        }

        // Selected Hydro Link
        if (oController.m_oSelectedHydroLink != null)
        {
            oController.switchHydroLinkState(false, oController.m_oSelectedHydroLink);
        }

        // Selected Radar Link
        if (oController.m_oSelectedRadarLink != null)
        {
            oController.switchRadarLinkState(false, oController.m_oSelectedRadarLink);
        }

        // Selected Satellite Link
        if (oController.m_oSelectedSatelliteLink != null)
        {
            oController.switchSatelliteLinkState(false, oController.m_oSelectedSatelliteLink);
        }

    }

    MapController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.refreshFullMap(this);
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
    }

    MapController.$inject = [
        '$scope',
        '$rootScope',
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
        '$location',
        'TableService',
        'HydroService',
        'MapLayerService',
        '$translate'
    ];


    return MapController;
})();