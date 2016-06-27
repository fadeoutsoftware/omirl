/**
 * Created by p.campanella on 24/09/2014.
 */

var MaxTableController = (function() {
    function MaxTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location, oTableService, oSce, $translate, $interval) {
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
        this.m_aoAggregations = [];
        this.m_oSelectedAggregation = {};

        this.m_bSideBarCollapsed = true;

        this.m_bReverseOrder = false;
        this.m_sOrderBy = "name";

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


        this.isOrderedBy = function(colName)
        {
            return (this.m_sOrderBy == colName);
        }

        oControllerVar.getDateTimeInLocalFormat =  function(sData)
        {
            var sHour = sData.split("]")[0].replace("[", "");
            var oDate = moment.utc(sHour, "HH:mm");
            oDate.local();
            return oDate.format("HH:mm");
        };

        oControllerVar.convertTablesHourToLocal = function(sData)
        {
            var sLocalHour = oControllerVar.getDateTimeInLocalFormat(sData);
            var regex = /\[(.*?)\]/;
            var str = sData.replace(regex, "[" + sLocalHour +"]");
            return str;
        };
        
        
        
/*
        this.m_oStationsService.getAggregationsTypes().success(function (data, status) {

            for (var iTypes = 0; iTypes<data.length; iTypes ++) {
                if (data[iTypes].code == "PRO") {
                    oControllerVar.m_oSelectedType = data[iTypes];
                    break;
                }
            }

            oControllerVar.m_aoAggregations = data;

            // QUESTO CHIUDE LA BARRA DI NAVIGAZIONE VOLENDO
            var oElement = angular.element("#mapNavigation");

            if (oElement != null) {
                if (oElement.length>0) {
                    var iWidth = oElement[0].clientWidth;
                    oElement[0].style.left = "-" + iWidth + "px";
                }
            }

            oControllerVar.typeSelected();

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Aggregations Items to add to the Menu');
        });
*/

        this.m_aoAggregations = this.m_oStationsService.getAggregationsTypes();

        for (var iTypes = 0; iTypes<this.m_aoAggregations.length; iTypes ++) {
            if (this.m_aoAggregations[iTypes].code == "PRO") {
                oControllerVar.m_oSelectedAggregation = this.m_aoAggregations[iTypes];
                break;
            }
        }

        // QUESTO CHIUDE LA BARRA DI NAVIGAZIONE VOLENDO
        var oElement = angular.element("#mapNavigation");

        if (oElement != null) {
            if (oElement.length>0) {
                var iWidth = oElement[0].clientWidth;
                oElement[0].style.left = "-" + iWidth + "px";
            }
        }

        oControllerVar.aggregationSelected();
    }

    MaxTableController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.aggregationSelected();
        this.m_bNowMode = false;
    };

    MaxTableController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.aggregationSelected();
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
    };


    MaxTableController.prototype.getMaxTableList = function (sPath) {

        return this.m_aoMaxTable;
    }

    MaxTableController.prototype.aggregationSelected = function() {
        var oControllerVar = this;

         this.m_oTableService.getMaxStationsTable().success(function (data, status) {
             oControllerVar.m_aoMaxTable = data;
             oControllerVar.m_bDowloadEnabled = true;

             var oDate = new Date(data.updateDateTime + " UTC");
             oControllerVar.m_oTranslateService('MAXTABLE_SUBTITOLO', {data: oDate.toString()}).then(function(msg){
                 oControllerVar.m_oUpdateDateInfo = msg;
             });

         }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
         });
    }

    MaxTableController.prototype.exportCsv = function() {
        var sRefDate = this.m_oConstantsService.getReferenceDate();
        window.open(this.m_oTableService.exportCsvMax(sRefDate), '_blank', '');
    }


    MaxTableController.prototype.toggleSideBarClicked = function() {

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

    MaxTableController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    MaxTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }

    MaxTableController.prototype.TrustDangerousSnippet = function(data) {
        return this.m_oSce.trustAsHtml(data);
    }

    MaxTableController.prototype.stationClicked = function(sCode) {

        var sStationCode = sCode;
        var oControllerVar = this;
        var sSensorType = 'rain1h';
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


    MaxTableController.$inject = [
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
    return MaxTableController;
}) ();
