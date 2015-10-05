/**
 * Created by p.campanella on 24/09/2014.
 */

var SummaryTableController = (function() {
    function SummaryTableController($scope, $log, $location, oConstantService, oTableService, $translate, oDialogService, oChartService, $interval) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
        this.m_oTableService = oTableService;
        this.m_oTranslateService = $translate;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oInterval = $interval;
        this.m_aoAlertReturn = [];

        this.m_aoDistrictReturn = [];

        this.m_aoWindReturn = [];

        this.m_oLastDateRef = "";

        var oControllerVar = this;

        refreshSummary = function() {
            oControllerVar.m_oTableService.getSummaryTable().success(function (data, status) {
                oControllerVar.m_aoAlertReturn = data.alertInfo;
                oControllerVar.m_aoDistrictReturn = data.districtInfo;
                oControllerVar.m_aoWindReturn = data.windInfo;
                oControllerVar.m_oLastDateRef = data.updateDateTime;

            }).error(function (data, status) {
                oControllerVar.m_oLog.error('Error Contacting Omirl Server');
            });
        };

        refreshSummary();

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
    }

    SummaryTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }

    SummaryTableController.prototype.getTemperatureDistrictSummary = function() {
        return this.m_aoDistrictReturn;
    }

    SummaryTableController.prototype.getTemperatureAlertSummary = function() {
        return this.m_aoAlertReturn;
    }

    SummaryTableController.prototype.getMaxWindSummary = function() {
        return this.m_aoWindReturn;
    }


    SummaryTableController.prototype.toggleSideBarClicked = function() {

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

    SummaryTableController.prototype.getFormattedRefDate = function (oDate) {
        if (oDate == null)
            return '[ND]';

        var oFormatDate = new Date(oDate);
        var oHours = oFormatDate.getHours();
        var oMinutes = oFormatDate.getMinutes();
        if (oHours < 10)
            oHours = '0' + oHours;
        if (oMinutes < 10)
            oMinutes = '0' + oMinutes;
        //return '[' + oHours + ':' + oMinutes + ' Locali]';
        return '[' + oHours + ':' + oMinutes + ']';
    }

    SummaryTableController.prototype.getDayString = function () {

        return this.m_oScope.m_oController.m_oLastDateRef;
    }

    SummaryTableController.prototype.stationClicked = function(sName, sSensorType) {

        var sStationCode = "";
        var oControllerVar = this;
        var sSensorType = sSensorType;
        var sName = sName;
        var sMunicipality = "";
        oControllerVar.m_oTableService.getStationAnagByName(sName).success(function (data) {

            if (angular.isDefined(data))
            {
                sMunicipality = data.municipality;
                sName = data.name;
                sStationCode = data.shortCode
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

    SummaryTableController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        refreshSummary();
        this.m_bNowMode = false;
    };

    SummaryTableController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate(new Date());
        refreshSummary();
        this.m_bNowMode = true;
        this.m_oReferenceDate = this.m_oConstantsService.getReferenceDate();
    };

    SummaryTableController.$inject = [
        '$scope',
        '$log',
        '$location',
        'ConstantsService',
        'TableService',
        '$translate',
        'dialogService',
        'ChartService',
        '$interval'
    ];
    return SummaryTableController;
}) ();
