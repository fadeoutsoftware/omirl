/**
 * Created by p.campanella on 30/05/2014.
 */


var ChartController = (function() {
    function ChartController($scope, dialogService, oChartService, $timeout) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oDialogService = dialogService;
        this.m_oChartService = oChartService;

        this.oChartVM = [];

        this.m_sStationCode = this.m_oScope.model.stationCode;

        var oControllerVar = this;

        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart('CFUNZ','rain1h').success(function(data,status) {
            oControllerVar.oChartVM = data;
            oControllerVar.addSeriesToChart();
        }).error(function(data,status){
            alert('Error Contacting Omirl Server');
        });
/*
        $timeout(function() {

            oControllerVar.

        },1000)
*/
    }

    ChartController.prototype.addSeriesToChart = function () {

        var oChart = this.m_oChartService.getChart(this.m_sStationCode);

        if (oChart != null) {

            oChart.setTitle({text: this.oChartVM.title}, {text: this.oChartVM.subtitle},true);

            this.oChartVM.chartSeries.forEach(function(oSerie) {
                oChart.addSeries(oSerie);
            });
        }
    }

    ChartController.$inject = [
        '$scope',
        'dialogService',
        'ChartService',
        '$timeout'
    ];
    return ChartController;
}) ();

