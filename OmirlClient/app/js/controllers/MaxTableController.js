/**
 * Created by p.campanella on 24/09/2014.
 */

var MaxTableController = (function() {
    function MaxTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location, oTableService) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oLocation = $location;
        this.m_oTableService = oTableService;
        this.m_bDowloadEnabled = false;

        this.m_aoMaxTable = [];
        this.m_aoAggregations = [];
        this.m_oSelectedAggregation = {};

        this.m_bSideBarCollapsed = true;

        this.m_bReverseOrder = false;
        this.m_sOrderBy = "name";


        var oControllerVar = this;
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

    MaxTableController.prototype.getMaxTableList = function (sPath) {

        return this.m_aoMaxTable;
    }

    MaxTableController.prototype.aggregationSelected = function() {

        var oControllerVar = this;

        /*
         this.m_oStationsService.getSensorsTable(this.m_oSelectedType.code).success(function (data, status) {
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
         */

        this.m_aoMaxTable = this.m_oStationsService.getMaxStationsTable(this.m_oSelectedAggregation.code);

        oControllerVar.m_bDowloadEnabled = true;
    }

    MaxTableController.prototype.exportCsv = function() {
        window.open(this.m_oStationsService.exportCsvStationList(this.m_oSelectedAggregation.code), '_blank', '');
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



    MaxTableController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService',
        '$location',
        'TableService'
    ];
    return MaxTableController;
}) ();
