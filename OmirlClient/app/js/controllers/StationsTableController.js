/**
 * Created by p.campanella on 29/08/2014.
 */

var StationsTableController = (function() {
    function StationsTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oLocation = $location;
        this.m_bDowloadEnabled = false;

        this.m_bShowCancelNameFilter = false;
        this.m_bShowCancelBasinFilter = false;
        this.m_bShowCancelDistrictFilter = false;
        this.m_bShowCancelMunicipalityFilter = false;
        this.m_bShowCancelAreaFilter = false;

        this.m_aoStations = [];
        this.m_aoTypes = [];
        this.m_oSelectedType = {};

        this.m_bSideBarCollapsed = true;

        var oControllerVar = this;

        this.m_oStationsService.getStationsTypes().success(function (data, status) {
            oControllerVar.m_aoTypes = data;

            var oElement = angular.element("#mapNavigation");

            if (oElement != null) {
                if (oElement.length>0) {
                    var iWidth = oElement[0].clientWidth;
                    oElement[0].style.left = "-" + iWidth + "px";
                }
            }

        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });

    }

    StationsTableController.prototype.getStationList = function (sPath) {

        return this.m_aoStations;
    }

    StationsTableController.prototype.typeSelected = function() {

        var oControllerVar = this;

        this.m_oStationsService.getStationsTable(this.m_oSelectedType.code).success(function (data, status) {
            oControllerVar.m_aoStations = data.tableRows;
            var aoStations = oControllerVar.m_aoStations;

            angular.forEach(oControllerVar.m_aoStations, function(value, key) {
                var NameCode = value.stationCode + ' ' + value.name;
                aoStations[key].nameCode = NameCode;

                if (value.municipality == null) aoStations[key].municipality = "";
                if (value.basin == null) aoStations[key].basin = "";
                if (value.area == null) aoStations[key].area = "";
            });

            oControllerVar.m_bDowloadEnabled = true;
        }).error(function (data, status) {
            oControllerVar.m_oLog.error('Error Loading Sensors Items to add to the Menu');
        });

    }

    StationsTableController.prototype.exportCsv = function() {
        window.open(this.m_oStationsService.exportCsvStationList(this.m_oSelectedType.code), '_blank', '');
    }

    StationsTableController.prototype.stationClicked = function(sCode, sMunicipality, sName) {

        var sStationCode = sCode;

        var oControllerVar = this;

        var sTitle ="";

        if (angular.isDefined(sMunicipality) == false) {
            sMunicipality = "";
        }

        if (sMunicipality == null) sMunicipality = "";

        sTitle = sName + " (Comune di " + sMunicipality + ")";

        if (this.m_oDialogService.isExistingDialog(sStationCode)) {
            return;
        }

        var sSensorType = this.m_oSelectedType.code;

        // The data for the dialog
        var model = {
            "stationCode": sStationCode,
            "chartType": sSensorType,
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
            title: sTitle
        };

        this.m_oDialogService.open(sStationCode,"stationsChart.html", model, options)
    }

    StationsTableController.prototype.NameChanged = function(sNameFilter)
    {
        if (sNameFilter == "") this.m_bShowCancelNameFilter = false;
        else this.m_bShowCancelNameFilter = true;
    }

    StationsTableController.prototype.CancelNameFilter = function()
    {
        this.m_oScope.search.nameCode="";
        this.m_bShowCancelNameFilter = false;
    }


    StationsTableController.prototype.AreaChanged = function(sAreaFilter)
    {
        if (sAreaFilter == "") this.m_bShowCancelAreaFilter = false;
        else this.m_bShowCancelAreaFilter = true;
    }

    StationsTableController.prototype.CancelAreaFilter = function()
    {
        this.m_oScope.search.area="";
        this.m_bShowCancelAreaFilter = false;
    }

    StationsTableController.prototype.DistrictChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelDistrictFilter = false;
        else this.m_bShowCancelDistrictFilter = true;
    }

    StationsTableController.prototype.CancelDistrictFilter = function()
    {
        this.m_oScope.search.district="";
        this.m_bShowCancelDistrictFilter = false;
    }


    StationsTableController.prototype.BasinChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelBasinFilter = false;
        else this.m_bShowCancelBasinFilter = true;
    }

    StationsTableController.prototype.CancelBasinFilter = function()
    {
        this.m_oScope.search.basin="";
        this.m_bShowCancelBasinFilter = false;
    }

    StationsTableController.prototype.MunicipalityChanged = function(sFilter)
    {
        if (sFilter == "") this.m_bShowCancelMunicipalityFilter = false;
        else this.m_bShowCancelMunicipalityFilter = true;
    }

    StationsTableController.prototype.CancelMunicipalityFilter = function()
    {
        this.m_oScope.search.municipality="";
        this.m_bShowCancelMunicipalityFilter = false;
    }



    StationsTableController.prototype.toggleSideBarClicked = function() {

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

    StationsTableController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    StationsTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }


    StationsTableController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService',
        '$location'
    ];

    return StationsTableController;
}) ();