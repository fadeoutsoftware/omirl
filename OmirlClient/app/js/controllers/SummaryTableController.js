/**
 * Created by p.campanella on 24/09/2014.
 */

var SummaryTableController = (function() {
    function SummaryTableController($scope, $log, $location, oConstantService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;

        this.m_aoAlertReturn = [
            {
                description: 'A',
                min: 5,
                stationmin: 'Stazione 1',
                max: 30,
                stationmax: 'Stazione 2'
            },
            {
                description: 'B',
                min: 3,
                stationmin: 'Stazione 1',
                max: 22,
                stationmax: 'Stazione 2'
            },
            {
                description: 'C',
                min: 8,
                stationmin: 'Stazione 1',
                max: 32,
                stationmax: 'Stazione 2'
            },
            {
                description: 'D',
                min: 10,
                stationmin: 'Stazione 1',
                max: 22,
                stationmax: 'Stazione 2'
            },
            {
                description: 'E',
                min: 10,
                stationmin: 'Stazione 1',
                max: 22,
                stationmax: 'Stazione 2'
            }
        ];

        this.m_aoDistrictReturn = [
            {
                description: 'Genova',
                min: 5,
                stationmin: 'Stazione 1',
                max: 30,
                stationmax: 'Stazione 2'
            },
            {
                description: 'Savona',
                min: 3,
                stationmin: 'Stazione 1',
                max: 22,
                stationmax: 'Stazione 2'
            },
            {
                description: 'Imperia',
                min: 8,
                stationmin: 'Stazione 1',
                max: 32,
                stationmax: 'Stazione 2'
            },
            {
                description: 'La Spezia',
                min: 10,
                stationmin: 'Stazione 1',
                max: 22,
                stationmax: 'Stazione 2'
            }
        ];

        this.m_aoWindReturn = [
            {
                description: 'Costa',
                max: 5,
                stationmax: 'Stazione 1',
                gust: 30,
                stationgust: 'Stazione 2'
            },
            {
                description: 'Rilievi',
                max: 3,
                stationmax: 'Stazione 1',
                gust: 22,
                stationgust: 'Stazione 2'
            }
        ];
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
    SummaryTableController.$inject = [
        '$scope',
        '$log',
        '$location',
        'ConstantsService'
    ];
    return SummaryTableController;
}) ();
