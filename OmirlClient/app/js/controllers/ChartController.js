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
        this.m_sChartType = this.m_oScope.model.chartType;

        var oControllerVar = this;

        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart(this.m_sStationCode,this.m_sChartType).success(function(data,status) {
            oControllerVar.oChartVM = data;
            oControllerVar.addSeriesToChart();
        }).error(function(data,status){
            alert('Error Contacting Omirl Server');
        });

    }

    ChartController.prototype.addSeriesToChart = function () {

        var oChart = this.m_oChartService.getChart(this.m_sStationCode);

        if (oChart != null) {

/*
            this.oChartVM.axisYMinValue = -10;
            this.oChartVM.axisYMaxValue = 40;
            this.oChartVM.axisYTickInterval = 5;
            this.oChartVM.axisYTitle = 'Temperatura Media (°C)';
            this.oChartVM.tooltipValueSuffix = ' mm';
*/
            var oChartOptions = oChart.options;
            oChartOptions.tooltip.valueSuffix = this.oChartVM.tooltipValueSuffix;

            oChart = new Highcharts.Chart(oChartOptions);
            this.m_oChartService.setChart(this.m_sStationCode,oChart);


            oChart.setTitle({text: this.oChartVM.title}, {text: this.oChartVM.subtitle},true);


            if (angular.isDefined(oChart.yAxis[0])) {
                var oYAxisOptions = {
                    min: this.oChartVM.axisYMinValue,
                    max: this.oChartVM.axisYMaxValue,
                    tickInterval: this.oChartVM.axisYTickInterval,
                    title: {
                        text: this.oChartVM.axisYTitle
                    },
                    opposite: false
                };

                oChart.yAxis[0].setOptions(oYAxisOptions);

            }

            this.oChartVM.dataSeries.forEach(function(oSerie) {
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

