/**
 * Created by p.campanella on 24/09/2014.
 */

var SummaryTableController = (function() {
    function SummaryTableController($scope, $log, $location, oConstantService, oTableService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
        this.m_oTableService = oTableService;

        this.m_aoAlertReturn = [];

        this.m_aoDistrictReturn = [];

        this.m_aoWindReturn = [];

        var oControllerVar = this;

        this.m_oTableService.getSummaryTable().success(function(data,status) {
            oControllerVar.m_aoAlertReturn = data.alertInfo;
            oControllerVar.m_aoDistrictReturn = data.districtInfo;
            oControllerVar.m_aoWindReturn = data.windInfo;

        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
        });
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

        var oFormatDate = new Date();
        var oDay = oFormatDate.getDate();
        var oMonth = oFormatDate.getMonth()+1;
        var oYear = oFormatDate.getFullYear();

        return oDay+'/' + oMonth + '/' + oYear;
    }

    SummaryTableController.$inject = [
        '$scope',
        '$log',
        '$location',
        'ConstantsService',
        'TableService'
    ];
    return SummaryTableController;
}) ();
