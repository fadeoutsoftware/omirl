/**
 * Created by p.campanella on 07/11/2014.
 */

var ModelsTableController = (function() {
    function ModelsTableController($scope, oConstantsService, $log, oStationsService, oDialogService, oChartService, $location, oTableService, oHydroService) {
        this.m_oScope = $scope;
        this.m_oConstantsService = oConstantsService;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oStationsService = oStationsService;
        this.m_oDialogService = oDialogService;
        this.m_oChartService = oChartService;
        this.m_oLocation = $location;
        this.m_oTableService = oTableService;
        this.m_oHydroService = oHydroService;
        this.m_bDowloadEnabled = false;

        this.m_bShowCancelBasinFilter = false;
        this.m_bShowCancelSectionFilter = false;

        this.m_aoModelLinks = [];

        this.m_aoModelRows = [];

        this.m_bSideBarCollapsed = true;

        this.m_bReverseOrder = false;
        this.m_sOrderBy = "orderNumber";


        var oControllerVar = this;

        this.m_sModelName = "Modello";
        this.m_sModelCode;

        this.m_oHydroService.getModelLinks().success(function (data) {
            oControllerVar.m_aoModelLinks = data;

            //set default selected clicked
            angular.forEach(oControllerVar.m_aoModelLinks, function(value, key) {
                if (value.isDefault == true) {
                    value.isActive = value.isDefault;
                    oControllerVar.m_sModelName = value.description;
                    oControllerVar.m_sModelCode = value.code;
                    oControllerVar.m_oHydroService.getModelTable(value.code).success(function(data){
                        oControllerVar.m_aoModelRows = data;
                    });
                }
            });

        });

        this.showSectionChart = function(sStationCode, sBasinCode, sName){
            var oControllerVar = this;
            var sSectionCode = sStationCode;
            var sBasin = sBasinCode;
            var sName = sName;

            if (this.m_oDialogService.isExistingDialog(sSectionCode)) {
                return;
            }

            var sModel = oControllerVar.m_sModelCode;


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
                title:  sName + " - " + sBasin + "",
                position: {my: "left top", at: "left top"},
                width: 'auto',
                height: 600,
                dialogClass:'sectionChartDialog'
            };

            this.m_oDialogService.open(sSectionCode,"sectionChart.html", model, options)
        };

    }

    ModelsTableController.prototype.getModelLinks = function () {
        return this.m_aoModelLinks;
    }

    ModelsTableController.prototype.modelLinkClicked = function (oModel) {
        var oControllerVar = this;

        /*
        this.m_oHydroService.getModelTable(oModel.code).success(function (data, status) {
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

        angular.forEach(oControllerVar.m_aoModelLinks, function(value, key) {
            value.isActive = false;
        });

        oModel.isActive = true;
        oControllerVar.m_sModelName = oModel.description;
        oControllerVar.m_sModelCode = oModel.code;
        this.m_oHydroService.getModelTable(oModel.code).success(function(data){
            oControllerVar.m_aoModelRows = data;
        });

    }

    ModelsTableController.prototype.getModelName = function() {
        return this.m_sModelName;
    }

    ModelsTableController.prototype.getModelRows = function() {
        return this.m_aoModelRows;
    }


    ModelsTableController.prototype.exportCsv = function() {
        window.open(this.m_oStationsService.exportCsvStationList(this.m_oSelectedType.code), '_blank', '');
    }

    ModelsTableController.prototype.sectionClicked = function(sSectionCode, sBasin, sName, sLinkCode) {

        if (angular.isDefined(sSectionCode) == false) {
            return
        }

        this.showSectionChart(sSectionCode, sBasin, sName);

        //alert('Show ' + sSectionCode);
    }

    ModelsTableController.prototype.BasinChanged = function(sBasinFilter)
    {
        if (sBasinFilter == "") this.m_bShowCancelBasinFilter = false;
        else this.m_bShowCancelBasinFilter = true;
    }

    ModelsTableController.prototype.CancelBasinFilter = function()
    {
        this.m_oScope.search.Basin="";
        this.m_bShowCancelBasinFilter = false;
    }


    ModelsTableController.prototype.SectionChanged = function(sSectionFilter)
    {
        if (sSectionFilter == "") this.m_bShowCancelSectionFilter = false;
        else this.m_bShowCancelSectionFilter = true;
    }

    ModelsTableController.prototype.CancelSectionFilter = function()
    {
        this.m_oScope.search.Section1 ="";
        this.m_bShowCancelSectionFilter = false;
    }



    ModelsTableController.prototype.toggleSideBarClicked = function() {

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

    ModelsTableController.prototype.isSideBarCollapsed = function () {
        return this.m_bSideBarCollapsed;
    }

    ModelsTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }

    ModelsTableController.prototype.getTableLinks = function () {
        return this.m_aoTableLinks;
    }

    ModelsTableController.$inject = [
        '$scope',
        'ConstantsService',
        '$log',
        'StationsService',
        'dialogService',
        'ChartService',
        '$location',
        'TableService',
        'HydroService'
    ];

    return ModelsTableController;
}) ();