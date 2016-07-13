/**
 * Created by s.adamo on 05/11/2015.
 */
/**
 * Created by p.campanella on 24/09/2014.
 */

var MaxHydroAlertZoneController = (function() {
    function MaxHydroAlertZoneController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location, oTableService, oSce, $translate, $interval) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oLocation = $location;
        this.m_oTableService = oTableService;
        this.m_oInterval = $interval;
        this.m_oSce = oSce;
        this.m_oTranslateService = $translate;
        this.m_bDowloadEnabled = false;

        this.m_aoMaxTable = [];
        this.m_oSelectedAggregation = {};

        this.m_bSideBarCollapsed = true;

        this.m_bReverseOrder = false;
        this.m_sOrderBy = "station";

        this.m_oUpdateDateTime = "";

        var oControllerVar = this;

        if (this.m_oConstantsService.isNowMode()) {
            oControllerVar.m_oReferenceDate = new Date();
            oControllerVar.m_bNowMode = true;
        }
        else {
            oControllerVar.m_oReferenceDate = oControllerVar.m_oConstantsService.getReferenceDate();
            oControllerVar.m_bNowMode = false;
        }

        // Add Auto Refresh Interval Callback
        this.m_oStopTimerPromise = this.m_oInterval(function () {

                if (oControllerVar.m_oConstantsService.isNowMode()) {
                    oControllerVar.m_oReferenceDate = new Date();
                    oControllerVar.m_bNowMode = true;
                }

            },
            this.m_oConstantsService.getRefreshRateMs());


        // QUESTO CHIUDE LA BARRA DI NAVIGAZIONE VOLENDO
        var oElement = angular.element("#mapNavigation");

        if (oElement != null) {
            if (oElement.length>0) {
                var iWidth = oElement[0].clientWidth;
                oElement[0].style.left = "-" + iWidth + "px";
            }
        }

        oControllerVar.Load();

    }

    MaxHydroAlertZoneController.prototype.Load = function() {
        var oControllerVar = this;

        this.m_oTableService.getMaxHydroAlertZones().success(function (data, status) {
            oControllerVar.m_aoMaxTable = data;
            oControllerVar.m_bDowloadEnabled = true;

            var oDate = new Date(data.updateDateTime + " UTC");
            oControllerVar.m_oTranslateService('MAXHYDROALERT_UPDATEINFO', {data: oDate.toString()}).then(function(msg){
                oControllerVar.m_oUpdateDateInfo = msg;
            });

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });
    }

    MaxHydroAlertZoneController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.Load();
        this.m_bNowMode = false;
    };

    MaxHydroAlertZoneController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.Load();
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
    };


    MaxHydroAlertZoneController.prototype.getMaxTableList = function (sPath) {

        return this.m_aoMaxTable;
    }

    MaxHydroAlertZoneController.prototype.exportCsv = function() {
        var sRefDate = this.m_oConstantsService.getReferenceDate();
        window.open(this.m_oTableService.exportCsvMaxHydroZones(sRefDate), '_blank', '');
    }


    MaxHydroAlertZoneController.prototype.toggleSideBarClicked = function() {

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

    MaxHydroAlertZoneController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    MaxHydroAlertZoneController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }

    MaxHydroAlertZoneController.prototype.TrustDangerousSnippet = function(data) {
        return this.m_oSce.trustAsHtml(data);
    }

    MaxHydroAlertZoneController.prototype.ConvertToLocalTime = function(date) {
        var oDate = new Date(Date.parse(date));

        var hours = oDate.getHours();
        var minutes = oDate.getMinutes();

        if (minutes < 10) {
            minutes = "0" + minutes;
        }

        hours =  oDate.getUTCHours();
        if (hours < 10) {
            hours = "0" + hours;
        }

        return oDate.toLocaleDateString('it-IT') + " " + hours + ":" + minutes;
    }

    MaxHydroAlertZoneController.prototype.ConvertToMeters = function(value) {

        var num = value / 10
        return num.toFixed(2);
    }


    MaxHydroAlertZoneController.prototype.stationClicked = function(sCode) {

        var sStationCode = sCode;
        var oControllerVar = this;
        var sSensorType = 'Idro';
        var sName = "";
        var sMunicipality = "";
        oControllerVar.m_oTableService.getStationAnag(sStationCode).success(function (data) {

            if (angular.isDefined(data))
            {
                sMunicipality = data.municipality;
                sName = data.name;
            }

            // The data for the dialog
            var model = {
                "stationCode": sStationCode,
                "chartType": sSensorType,
                "municipality": sMunicipality,
                "name": sName
            };

            oControllerVar.m_oTranslateService('DIALOGTITLE', {name: sName, municipality: sMunicipality}).then(function (text) {
                // jQuery UI dialog options
                var options = {
                    autoOpen: false,
                    modal: false,
                    width: 'auto',
                    resizable: false,
                    close: function (event, ui) {
                        // Remove the chart from the Chart Service
                        oControllerVar.m_oChartService.removeChart(sStationCode);
                    },
                    title: text
                };

                oControllerVar.m_oDialogService.open(sStationCode, "stationsChart.html", model, options);
            });


        });
    };

    


    MaxHydroAlertZoneController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService',
        '$location',
        'TableService',
        '$sce',
        '$translate',
        '$interval'
    ];
    return MaxHydroAlertZoneController;
}) ();
