/**
 * Created by p.campanella on 23/03/2015.
 */
var SectionChartController = (function() {
    function SectionChartController($scope, dialogService, oChartService, $timeout, oConstantsService, $log) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oDialogService = dialogService;
        this.m_oChartService = oChartService;
        this.m_oConstantsService = oConstantsService;
        this.m_aoOtherCharts = [];
        this.m_bLoading = true;
        this.m_oLog = $log;
        this.m_oDialogModel = this.m_oScope.model;
        this.m_sDialogTitle = "";

        this.oChartVM = [];

        this.m_sImageLink = "img/nodata.jpg"

        this.m_sSectionCode = this.m_oScope.model.sectionCode;
        this.m_sChartType = this.m_oScope.model.chartType;

        this.m_iHeight = 490;
        this.m_iWidth = 730;

        var oControllerVar = this;

        oControllerVar.LoadData();
    }

    SectionChartController.prototype.getImageLink = function() {
        return this.m_sImageLink;
    }

    SectionChartController.prototype.LoadData = function () {
        var oControllerVar = this;

        oControllerVar.m_oChartService.getSectionChart(this.m_sSectionCode,this.m_sChartType).success(function(data,status) {

            if (!angular.isDefined(data)){
                alert('Impossibile caricare il grafico della sezione ' + oControllerVar.m_sSectionCode);
                oControllerVar.m_bLoading = false;
                return;
            }
            if (data=="") {
                alert('Impossibile caricare il grafico della sezione ' + oControllerVar.m_sSectionCode);
                oControllerVar.m_bLoading = false;
                return;
            }

            oControllerVar.oChartVM = data;
            oControllerVar.m_aoOtherCharts = [];
            oControllerVar.m_sImageLink = data.sImageLink;

            var oDialog = oControllerVar.m_oDialogService.getExistingDialog(oControllerVar.m_sSectionCode);

            if(angular.isDefined(oControllerVar.oChartVM.otherChart)) {

                oControllerVar.oChartVM.otherChart.forEach(function(sType){
                    var oOtherChartLink = {};
                    oOtherChartLink.sensorType = sType;

                    if (oControllerVar.m_sChartType == sType)
                    {
                        oOtherChartLink.isActive = true;
                    }
                    else
                    {
                        oOtherChartLink.isActive = false;
                    }

                    var oHydroLink = oControllerVar.m_oConstantsService.getHydroLinkByType(sType);

                    if (oHydroLink != null)
                    {
                        oOtherChartLink.description = oHydroLink.description;
                        oOtherChartLink.imageLinkOff = oHydroLink.link;
                    }

                    oControllerVar.m_aoOtherCharts.push(oOtherChartLink);
                });

            }


            oControllerVar.m_bLoading = false;
        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
        });
    }


    SectionChartController.prototype.isLoadingVisibile = function () {
        return this.m_bLoading;
    }

    SectionChartController.prototype.getOtherLinks = function() {
        return this.m_aoOtherCharts;
    }

    SectionChartController.prototype.otherLinkClicked = function(oOtherLink) {

        var oControllerVar = this;
        this.m_bLoading = true;


        oControllerVar.m_oChartService.getSectionChart(this.m_sSectionCode,oOtherLink.sensorType).success(function(data,status) {

            if (!angular.isDefined(data)){
                alert('Impossibile caricare il grafico della sezione ' + oControllerVar.m_sSectionCode);
                oControllerVar.m_bLoading = false;
                return;
            }
            if (data=="") {
                alert('Impossibile caricare il grafico della sezione ' + oControllerVar.m_sSectionCode);
                oControllerVar.m_bLoading = false;
                return;
            }

            oControllerVar.oChartVM = data;
            oControllerVar.m_aoOtherCharts = [];
            oControllerVar.m_sImageLink = data.sImageLink;

            var oDialog = oControllerVar.m_oDialogService.getExistingDialog(oControllerVar.m_sSectionCode);

            if(angular.isDefined(oControllerVar.oChartVM.otherChart)) {

                oControllerVar.oChartVM.otherChart.forEach(function(sType){
                    var oOtherChartLink = {};
                    oOtherChartLink.sensorType = sType;

                    if (oControllerVar.m_sChartType == sType)
                    {
                        oOtherChartLink.isActive = true;
                    }
                    else
                    {
                        oOtherChartLink.isActive = false;
                    }

                    var oHydroLink = oControllerVar.m_oConstantsService.getHydroLinkByType(sType);

                    if (oHydroLink != null)
                    {
                        oOtherChartLink.description = oHydroLink.description;
                        oOtherChartLink.imageLinkOff = oHydroLink.link;
                    }

                    oControllerVar.m_aoOtherCharts.push(oOtherChartLink);
                });
            }

            oControllerVar.m_bLoading = false;
        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
        });

    }

    SectionChartController.prototype.zoomIn = function() {
        //var oDialog = this.m_oDialogService.getExistingDialog(this.m_sSectionCode);
        this.m_iHeight *= 1.1;
        this.m_iWidth *= 1.1;
        this.LoadData();
        this.addSeriesToChart();
    }

    SectionChartController.prototype.zoomOut = function() {
        //alert('out');

        this.m_iHeight /= 1.1;
        this.m_iWidth /= 1.1;
        this.LoadData();
        this.addSeriesToChart();
    }

    SectionChartController.prototype.getHeight = function() {
        return this.m_iHeight.toString() + "px";
    }

    SectionChartController.prototype.getMinWidth = function() {
        return "310px";
    }

    SectionChartController.prototype.getWidth = function() {
        return this.m_iWidth.toString() + "px";
    }


    SectionChartController.$inject = [
        '$scope',
        'dialogService',
        'ChartService',
        '$timeout',
        'ConstantsService',
        '$log'
    ];
    return SectionChartController;
}) ();

