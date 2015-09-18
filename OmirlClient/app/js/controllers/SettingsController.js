/**
 * Created by p.campanella on 20/08/2014.
 */

var SettingsController = (function() {
    function SettingsController($scope, oConstantsService, oAuthService, oLocation, layerService, mapService, oMapNavigatorService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        // Layer Service
        this.m_oLayerService = layerService;
        // Map Service
        this.m_oMapService = mapService;
        // Constants service
        this.m_oContstantsService = oConstantsService;
        // Auth service
        this.m_oAuthService = oAuthService;
        // Location service
        this.m_oLocation = oLocation;
        // Map Navigator Service
        this.m_oMapNavigatorService = oMapNavigatorService;


        // Old Password Value
        this.oldPw = "";
        // New Password Value
        this.newPw = "";
        // Confirmed Password Value
        this.confirmPw = "";
        // Flag to enable or disable the Change Password group
        this.changePassword = false;
        // Flag to know if the settings are changed
        this.m_bSettingsChanged = false;
        // Settings output message
        this.m_sSettingsMessage = "";
        // Settings output style name
        this.m_sSettingsMessageStyle = "";

        // Static Links
        this.m_aoStaticLinks = [];
        // Selected static layer
        this.m_oSelectedStaticLayer = [];
        // Sensor Links
        this.m_aoSensorsLinks = [];
        // Selected Sensor Link
        this.m_oSelectedSensorLink = {};
        // Maps
        this.m_aoMaps = [];
        // Selected Map
        this.m_oSelectedMap = {};
        // Settings output message
        this.m_sMapSettingsMessage = "";
        // Settings output style name
        this.m_sMapSettingsMessageStyle = "";

        this.defaultLat = 8.60;
        this.defaultLon = 44.20;
        this.defaultZoom = 9;

        this.featureStyle = {
            fillColor: '#0099ff',
            fillOpacity: 0.9,
            strokeColor: '#050505',
            pointRadius: 8
        };

        var oControllerVar = this;

        // Check if the user exists
        if (this.m_oContstantsService.getUser() != null)
        {
            // Initialize users settings
            this.email = this.m_oContstantsService.getUser().mail;
            this.userName = this.m_oContstantsService.getUser().name;
            if (this.m_oContstantsService.getUser().defaultLat!=null)
            {
                this.defaultLat = this.m_oContstantsService.getUser().defaultLat;
            }
            if (this.m_oContstantsService.getUser().defaultLon != null)
            {
                this.defaultLon = this.m_oContstantsService.getUser().defaultLon;
            }
            if (this.m_oContstantsService.getUser().defaultZoom != null)
            {
                this.defaultZoom = this.m_oContstantsService.getUser().defaultZoom;
            }
            if (this.m_oContstantsService.getUser().defaultSensorType != null)
            {
                this.m_oSelectedSensorLink = this.m_oContstantsService.getSensorLinkByType(this.m_oContstantsService.getUser().defaultSensorType);
            }

            if (this.m_oContstantsService.getUser().defaultStatics != null)
            {
                var sStatics = this.m_oContstantsService.getUser().defaultStatics;
                var asStaticLayers = sStatics.split(";");

                for (var iLayers = 0; iLayers<asStaticLayers.length; iLayers++)
                {
                    var oStaticLink = this.m_oContstantsService.getStaticLinkById(asStaticLayers[iLayers]);

                    if (oStaticLink!=null)
                    {
                        this.m_oSelectedStaticLayer.push(oStaticLink);
                    }

                }

            }

        }
        else
        {
            // No user no settings: go home!
            this.m_oLocation.path("/");
            OmirlMoveTo('#top');
        }

        // Initialize Layer Service
        if (this.m_oLayerService.getBaseLayers().length == 0) {
            var oBaseLayer1 = new OpenLayers.Layer.Google("Physical", {type: google.maps.MapTypeId.TERRAIN});
            var oBaseLayer2 = new OpenLayers.Layer.Google("Hybrid", {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20});
            var oBaseLayer3 = new OpenLayers.Layer.Google("Streets", {numZoomLevels: 20});
            var oBaseLayer4 = new OpenLayers.Layer.Google("Satellite", {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 20});

            /* Base layers inclusion */
            var oOSMLayer = new OpenLayers.Layer.XYZ(
                'OSM',
                'http://www.toolserver.org/tiles/bw-mapnik/${z}/${x}/${y}.png',
                {
                    attribution: 'basemap data &copy; <a href="http://osm.org/copyright" target="_blank">OpenStreetMap</a>',
                    sphericalMercator: true,
                    wrapDateLine: true,
                    transitionEffect: "resize",
                    buffer: 0,
                    numZoomLevels: 20
                }
            );

            this.m_oLayerService.addBaseLayer(oOSMLayer);
            this.m_oLayerService.addBaseLayer(oBaseLayer1);
            this.m_oLayerService.addBaseLayer(oBaseLayer2);
            this.m_oLayerService.addBaseLayer(oBaseLayer3);
            this.m_oLayerService.addBaseLayer(oBaseLayer4);
        }


        // Clear the map on exit
        $scope.$on('$locationChangeStart', function (event, next, current) {

            if (oControllerVar.m_oMapService.map != null) {
                oControllerVar.m_oLayerService.clarAll();
                oControllerVar.m_oMapService.map.destroy();
                oControllerVar.m_oMapService.map = null;
                oControllerVar.m_oMapService.stationsPopupControllerAdded = false;
                oControllerVar.m_oMapService.readyCallback = null;
            }
        });

        this.m_oMapService.readyCallback = this.mapReadyCallback;
        this.m_oMapService.callbackArg = oControllerVar;


        var aoStaticLinks = this.m_oContstantsService.getStaticLinks();

        for (var iElement = 0; iElement < aoStaticLinks.length; iElement++) {
            this.m_aoStaticLinks.push(aoStaticLinks[iElement]);
        }

        var aoSensorsLinks = this.m_oContstantsService.getSensorsLinks();

        for (var iElement = 0; iElement < aoSensorsLinks.length; iElement++) {
            this.m_aoSensorsLinks.push(aoSensorsLinks[iElement]);
        }

        //Date ref
        var oReferenceDate = new Date();

        if (this.m_oContstantsService.getReferenceDate() != "")
        {

            oReferenceDate = this.m_oContstantsService.getReferenceDate();
            this.checkNow = false;
        }
        else
        {
         this.checkNow = true;
        }

        var oMonth = oReferenceDate.getMonth() + 1;
        var oDay = oReferenceDate.getDate();

        if(oMonth<10)
        {
            oMonth = '0' + oMonth;
        }
        if(oDay<10)
        {
            oDay = '0' + oDay;
        }

        this.dataRef = oReferenceDate.getFullYear() + '-' + oMonth + '-' + oDay;
        this.ore = oReferenceDate.getHours();
        this.min = oReferenceDate.getMinutes();
    }


    SettingsController.prototype.mapReadyCallback = function(oControllerVar) {
        // Create the point layer
        var oPointLayer = new OpenLayers.Layer.Vector("Map Center", { eventListeners: {"featureadded": oControllerVar.mapPointClicked}});

        // Add the layer to the map
        oControllerVar.m_oMapService.map.addLayer(oPointLayer);


        // Projection change for points
        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = oControllerVar.m_oMapService.map.getProjectionObject(); //The map projection (Spherical Mercator)

        // Create the center feature
        var oFeature = new OpenLayers.Feature.Vector(
            new OpenLayers.Geometry.Point(oControllerVar.defaultLat, oControllerVar.defaultLon).transform(epsg4326, projectTo),
            {},
            oControllerVar.featureStyle
        );


        oFeature.attributes.Controller = oControllerVar;

        var aoFeatures = [];
        aoFeatures.push(oFeature);

        oPointLayer.addFeatures(aoFeatures);

        // Create also draw control
        var oDrawPointControl = new OpenLayers.Control.DrawFeature(oPointLayer, OpenLayers.Handler.Point);
        // Add the control to the map
        oControllerVar.m_oMapService.map.addControl(oDrawPointControl);
        // And activate it
        oDrawPointControl.activate();
    }


    SettingsController.prototype.mapPointClicked = function(event) {

        var feature = event.feature;

        if (feature.layer.features.length == 1) return;

        if (angular.isDefined(feature.layer.features)) {
            if (feature.layer.features.length>1)
            {

                feature.attributes.Controller = feature.layer.features[0].attributes.Controller;
                feature.layer.removeFeatures(feature.layer.features[0]);
            }
        }

        feature.style = {
            fillColor: '#0099ff',
            fillOpacity: 0.9,
            strokeColor: '#050505',
            pointRadius: 8
        };

        feature.layer.drawFeature(feature);

        var epsg4326 =  new OpenLayers.Projection("EPSG:4326"); //WGS 1984 projection
        var projectTo = this.map.getProjectionObject(); //The map projection (Spherical Mercator)

        var oCenter = new OpenLayers.Geometry.Point(feature.geometry.x,feature.geometry.y).transform(projectTo, epsg4326);
        feature.attributes.Controller.updateLatLon(oCenter.x,oCenter.y);
    }

    SettingsController.prototype.updateLatLon = function(dLat, dLon) {
        this.defaultLat = Math.round(dLat * 100) / 100;
        this.defaultLon = Math.round(dLon * 100) / 100;
        this.m_oScope.$apply();
    }

    /**
     * Used on template to get account settings output message
     * @returns {string}
     */
    SettingsController.prototype.getSettingsMessage = function() {
        return this.m_sSettingsMessage;
    }

    /**
     * Used on template to get map settings output message
     * @returns {string}
     */
    SettingsController.prototype.getMapSettingsMessage = function() {
        return this.m_sMapSettingsMessage;
    }

    /**
     * Called to save Account Settings
     */
    SettingsController.prototype.saveSettings = function() {
        // Check for any change
        if (!this.m_bSettingsChanged) {
            this.m_sSettingsMessage = "Nessuna modifica effettuata";
            this.m_sSettingsMessageStyle = "";
            return;
        }

        // Set Settings Json variale
        var oUserSettings =  {};

        // Get name and change pw flag
        oUserSettings.userName = this.userName;
        oUserSettings.changePassword = this.changePassword;

        if (this.changePassword)
        {
            // Need a new pw!
            if (this.newPw == "")
            {
                this.m_sSettingsMessage = "Inserire una password valida";
                this.m_sSettingsMessageStyle = "bs-callout-error";
                return;
            }

            // They must be the same
            if (this.newPw != this.confirmPw)
            {
                this.m_sSettingsMessage = "Le password non coincidono";
                this.m_sSettingsMessageStyle = "bs-callout-error";
                return;
            }

            // Ok fill settings object
            oUserSettings.oldPassword = this.oldPw;
            oUserSettings.newPassword = this.newPw;
            oUserSettings.confirmPassword = this.confirmPw;
        }
        else
        {
            // Clear the form!
            oUserSettings.oldPassword = "";
            oUserSettings.newPassword = "";
            oUserSettings.confirmPassword = "";
        }

        var oController = this;

        // Save!
        oController.m_oAuthService.saveUserSettings(oUserSettings).success(function(data, status) {
            // Was ok?
            if (data.BoolValue == true)
            {
                // Saved: update informations
                oController.m_oContstantsService.getUser().name = oUserSettings.userName;
                oController.m_sSettingsMessage = "I dati sono stati salvati correttamente";
                oController.m_sSettingsMessageStyle = "bs-callout-ok";

                // Clear the form
                oController.oldPw = "";
                oController.newPw = "";
                oController.confirmPw = "";

                // Reset the flags
                oController.changePassword = false;
                oController.m_bSettingsChanged = false;
            }
            else
            {
                // Output error!
                oController.m_sSettingsMessage = data.StringValue;
                oController.m_sSettingsMessageStyle = "bs-callout-error";
            }
        }).error(function(data, status) {
            // Output error!
            oController.m_sSettingsMessage = "Si è verificato un errore nel salvataggio dei dati";
            oController.m_sSettingsMessageStyle = "bs-callout-error";
        });
    }

    /**
     * Callback on the account form values change
     */
    SettingsController.prototype.onSettingsChanged = function() {
        this.m_sSettingsMessage = "";
        this.m_bSettingsChanged = true;
        this.m_sSettingsMessageStyle = "";
    }

    SettingsController.prototype.getSettingsChanged = function() {
        return this.m_bSettingsChanged;
    }

    SettingsController.prototype.SaveMapSettings = function() {

        var oController = this;

        if (angular.isDefined(this.m_oSelectedMap))
        {
            //this.m_oContstantsService.getUser().defaultMap = this.m_oSelectedMap;
        }

        if (angular.isDefined(this.m_oSelectedSensorLink))
        {
            this.m_oContstantsService.getUser().defaultSensorType = this.m_oSelectedSensorLink.code;
        }

        if (angular.isDefined(this.m_oSelectedStaticLayer))
        {
            var sStaticLinks = "";

            for (var iElement = 0; iElement < this.m_oSelectedStaticLayer.length; iElement++) {
                sStaticLinks += this.m_oSelectedStaticLayer[iElement].layerID + ";";
            }

            this.m_oContstantsService.getUser().defaultStatics = sStaticLinks;
        }

        if (angular.isDefined(this.defaultLat))
        {
            this.m_oContstantsService.getUser().defaultLat = this.defaultLat;
        }

        if (angular.isDefined(this.defaultLon))
        {
            this.m_oContstantsService.getUser().defaultLon = this.defaultLon;
        }

        if (angular.isDefined(this.defaultZoom))
        {
            this.m_oContstantsService.getUser().defaultZoom = this.defaultZoom;
        }


        this.m_oAuthService.saveMapUserSettings(this.m_oContstantsService.getUser()).success(function(data, status) {
            // Was ok?
            if (data.BoolValue == true)
            {
                // Saved: update informations
                oController.m_sMapSettingsMessage = "I dati sono stati salvati correttamente";
                oController.m_sMapSettingsMessageStyle = "bs-callout-ok";
            }
            else
            {
                // Output error!
                oController.m_sMapSettingsMessage = data.StringValue;
                oController.m_sMapSettingsMessageStyle = "bs-callout-error";
            }
        }).error(function(data, status) {
            // Output error!
            oController.m_sMapSettingsMessage = "Si è verificato un errore nel salvataggio dei dati";
            oController.m_sMapSettingsMessageStyle = "bs-callout-error";
        });

    }

    SettingsController.prototype.onNowChanged = function() {

        if (this.checkNow) {
            this.m_oContstantsService.setReferenceDate('');
            return;
        }
        else
        {
            var oDate = new Date(this.dataRef);
            this.m_oContstantsService.setReferenceDate(oDate);
        }

        this.onSettingsChanged();
    }

    SettingsController.prototype.pad = function (number, length){
        var str = "" + number;
        while (str.length < length) {
            str = '0'+str;
        }
        return str;
    }

    SettingsController.prototype.getTimezoneOffset  = function () {

        var offset = new Date().getTimezoneOffset()
        offset = ((offset<0? '+':'-')+ // Note the reversed sign!
            this.pad(parseInt(Math.abs(offset/60)), 2)+
            this.pad(Math.abs(offset%60), 2));

        return offset;
    }

    SettingsController.prototype.onDataChanged = function() {
        try {
            //var oDate = new Date(this.dataRef.getFullYear(), this.dataRef.getMonth() + 1, this.dataRef.getDate(), this.ore, this.min, 0);

            var sOre = this.ore;
            var sMinuti = this.min;

            if (this.ore<10)
            {
                sOre = "0"+sOre;
            }

            if (this.min<10)
            {
                sMinuti = "0"+sMinuti;
            }

            var sDateString = this.dataRef + 'T' + sOre + ':' + sMinuti + ':00.000' + this.getTimezoneOffset();
            var oDate = new Date(sDateString);
            if (oDate != null)
            {
                this.m_oContstantsService.setReferenceDate(oDate);
            }

            this.onSettingsChanged();
        } catch (err) {
            alert("Errore! Verificare la data inserita.");
        }
    }

    SettingsController.$inject = [
        '$scope',
        'ConstantsService',
        'AuthService',
        '$location',
        'az.services.layersService',
        'az.services.mapService',
        'MapNavigatorService'
    ];

    return SettingsController;
}) ();
