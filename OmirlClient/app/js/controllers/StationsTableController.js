/**
 * Created by p.campanella on 29/08/2014.
 */

var StationsTableController = (function() {
    function StationsTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;

        this.m_aoStations = [];
        this.m_aoTypes = [];
        this.m_oSelectedType = {};


        this.m_aoTypes = this.m_oStationsService.getStationsTypes();
    }

    StationsTableController.prototype.getStationList = function (sPath) {

        return this.m_aoStations;
    }

    StationsTableController.prototype.typeSelected = function() {
        this.m_aoStations = this.m_oStationsService.getStationsTable(this.m_oSelectedType.code);
    }

    StationsTableController.prototype.stationClicked = function(sCode) {

        var sStationCode = sCode;

        var oControllerVar = this;

        if (this.m_oDialogService.isExistingDialog(sStationCode)) {
            return;
        }

        var sSensorType = this.m_oSelectedType.code;

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
            resizable: false,
            close: function(event, ui) {
                // Remove the chart from the Chart Service
                oControllerVar.m_oChartService.removeChart(sStationCode);
            }
        };

        this.m_oDialogService.open(sStationCode,"stationsChart.html", model, options)
    }


    StationsTableController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService'
    ];

    return StationsTableController;
}) ();