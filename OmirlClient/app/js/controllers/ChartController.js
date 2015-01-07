/**
 * Created by p.campanella on 30/05/2014.
 */


var ChartController = (function() {
    function ChartController($scope, dialogService, oChartService, $timeout, oConstantsService, $log) {
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

        this.m_sStationCode = this.m_oScope.model.stationCode;
        this.m_sChartType = this.m_oScope.model.chartType;

        this.m_iHeight = 400;
        this.m_iWidth = 550;

        var oControllerVar = this;


        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart(this.m_sStationCode,this.m_sChartType).success(function(data,status) {

            if (!angular.isDefined(data)){
                alert('Impossibile caricare il grafico della stazione ' + oControllerVar.m_sStationCode);
                oControllerVar.m_bLoading = false;
                return;
            }
            if (data=="") {
                alert('Impossibile caricare il grafico della stazione ' + oControllerVar.m_sStationCode);
                oControllerVar.m_bLoading = false;
                return;
            }

            oControllerVar.oChartVM = data;

            var oDialog = oControllerVar.m_oDialogService.getExistingDialog(oControllerVar.m_sStationCode);


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

                    var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(sType);

                    if (oSensorLink != null)
                    {
                        oOtherChartLink.description = oSensorLink.description;
                        oOtherChartLink.imageLinkOff = oSensorLink.imageLinkOff;
                    }

                    oControllerVar.m_aoOtherCharts.push(oOtherChartLink);
                });

            }

            oControllerVar.addSeriesToChart();

            oControllerVar.m_bLoading = false;
        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
        });
    }


    ChartController.prototype.isLoadingVisibile = function () {
        return this.m_bLoading;
    }

    ChartController.prototype.getOtherLinks = function() {
        return this.m_aoOtherCharts;
    }

    ChartController.prototype.otherLinkClicked = function(oOtherLink) {

        var oControllerVar = this;
        this.m_bLoading = true;

        var bIsStockChart = true;
        bIsStockChart = oControllerVar.m_oChartService.isStockChart(oOtherLink.sensorType);

        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart(this.m_sStationCode,oOtherLink.sensorType).success(function(data,status) {

            oControllerVar.m_oScope.model.isStock = bIsStockChart;
            oControllerVar.oChartVM = data;
            oControllerVar.m_sChartType = oOtherLink.sensorType;

            oControllerVar.m_aoOtherCharts = [];

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

                    var oSensorLink = oControllerVar.m_oConstantsService.getSensorLinkByType(sType);

                    if (oSensorLink != null)
                    {
                        oOtherChartLink.description = oSensorLink.description;
                        oOtherChartLink.imageLinkOff = oSensorLink.imageLinkOff;
                    }

                    oControllerVar.m_aoOtherCharts.push(oOtherChartLink);
                });
            }

            oControllerVar.addSeriesToChart();

            oControllerVar.m_bLoading = false;
        }).error(function(data,status){
            oControllerVar.m_oLog.error('Error Contacting Omirl Server');
        });

    }

    ChartController.prototype.csvExport = function (sCode, sChart) {
        window.open(this.m_oChartService.exportCsvStationChart(this.m_sStationCode,this.m_sChartType), '_blank', '');
    }

    ChartController.prototype.addSeriesToChart = function () {

        var oControllerVar = this;

        // Get Chart reference for this Chart
        var oChart = this.m_oChartService.getChart(this.m_sStationCode);

        // Get From the model if it is a Stock or a Normal Chart
        var bIsStockChart = this.m_oScope.model.isStock;

        if (oChart != null)
        {
            // Find if we have a Column Chart
            var bColumnChart = false;

            if (angular.isDefined(this.oChartVM.dataSeries))
            {
                // For each time Serie Check if the type is column
                this.oChartVM.dataSeries.forEach(function(oSerie) {
                    if (oSerie.type == "column") bColumnChart = true;
                });

                // Get Back the Chart Options
                var oChartOptions;


                var oElement = oChart.options.chart.renderTo;
                // Add Columns settings
                if (bIsStockChart) {

                    oChartOptions = {
                        chart: {
                            renderTo: oElement
                            //,alignTicks: false
                        },
                        credits: {
                            enabled: false
                        },
                        subtitle:{
                            text: this.m_oScope.model.name + " (Comune di " + this.m_oScope.model.municipality + ") - ARPAL CFMI-PC"
                        },
                        plotOptions: {
                            series: {
                                dataGrouping: {
                                    enabled: true,
                                    approximation :"high"
                                }
                            },
                            line: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            },
                            line: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            },
                            area: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            }
                        },
                        rangeSelector: {
                            inputEnabled: true,
                            selected: 1,
                            buttons: [{
                                type: 'day',
                                count: 1,
                                text: '1g'
                            }, {
                                type: 'day',
                                count: 3,
                                text: '3gg'
                            }, {
                                type: 'week',
                                count: 1,
                                text: '7gg'
                            }/*, {
                                type: 'day',
                                count: 10,
                                text: '10gg'
                            }*/, {
                                type: 'day',
                                count: 15,
                                text: '15gg'
                            }/*, {
                                type: 'all',
                                text: 'Tutti'
                            }*/],
                            inputDateFormat:'%d/%m/%Y',
                            inputEditDateFormat:'%d/%m/%Y'
                        },
                        tooltip: {
                            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b>',
                            valueDecimals: 2,
                            valueSuffix: this.oChartVM.tooltipValueSuffix
                        },
                        exporting: {
                            buttons: {
                                contextButton: {
                                    symbol: 'url(img/chartdownload.png)'
                                }
                            }
                        }

                    };
                }
                else {

                    oChartOptions = {
                        chart: {
                            renderTo: oElement,
                            zoomType: "xy"
                            //,alignTicks: false
                        },
                        credits: {
                            enabled: false
                        },
                        tooltip: {
                            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b>',
                            valueDecimals: 2,
                            valueSuffix: this.oChartVM.tooltipValueSuffix
                        },
                        xAxis: {
                            type: 'datetime'
                        },
                        title:{
                            text:''
                        },
                        subtitle:{
                            text: this.m_oScope.model.name + " (Comune di " + this.m_oScope.model.municipality + ") - ARPAL CFMI-PC"
                        },
                        plotOptions: {
                            column: {
                                //pointRange: 1000*60*60,
                                //pointWidth: 5,
                                pointPlacement: -0.5,
                                grouping: false,
                                borderWidth: 0
                            },
                            spline: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            },
                            line: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            },
                            area: {
                                marker: {
                                    enabled: true,
                                    radius: 1
                                }
                            }
                        },
                        exporting: {
                            buttons: {
                                contextButton: {
                                    symbol: 'url(img/chartdownload.png)'
                                }
                            }
                        }

                    };
                }

                oChart.destroy();

                // Create chart again
                if (bIsStockChart) {
                    oChart = new Highcharts.StockChart(oChartOptions);
                }
                else {
                    oChart = new Highcharts.Chart(oChartOptions);
                }

                // Update the Chart in the service
                this.m_oChartService.setChart(this.m_sStationCode,oChart);



                if (Highcharts.getOptions().exporting.buttons.contextButton.menuItems.length == 6)
                {
                    Highcharts.getOptions().exporting.buttons.contextButton.menuItems.push({
                        text: 'Scarica CSV',
                        onclick: function () {
                            oControllerVar.csvExport();
                        }
                    });
                }

                // Y MAIN AXIS
                if (angular.isDefined(oChart.yAxis[0])) {

                    var oPlotLines = undefined;
                    if (this.oChartVM.axisYTickInterval==0) this.oChartVM.axisYTickInterval = 1;

                    // Add Horizontal Lines
                    if (angular.isDefined(this.oChartVM.horizontalLines)) {
                        if (this.oChartVM.horizontalLines.length > 0) {
                            oPlotLines = [];

                            this.oChartVM.horizontalLines.forEach(function(oHorizontalLine){
                                var oLine = {};
                                oLine.color = oHorizontalLine.color;
                                oLine.width = 2;
                                oLine.value = oHorizontalLine.value;

                                oPlotLines.push(oLine);
                            });
                        }
                    }

                    var iRotation=270;
                    var iMargin = 40;

                    if (bIsStockChart) {
                        iRotation = 270;
                        iMargin = 30;
                    }
                    // Set Main Axis Options
                    var oYAxisOptions = {
                        min: this.oChartVM.axisYMinValue,
                        max: this.oChartVM.axisYMaxValue,
                        tickInterval: this.oChartVM.axisYTickInterval,
                        title: {
                            text: this.oChartVM.axisYTitle,
                            rotation: iRotation,
                            margin: iMargin
                        },
                        opposite: this.oChartVM.axisIsOpposite,
                        plotLines: oPlotLines,
                        minPadding: 0,
                        startOnTick: true
                    };

                    oChart.yAxis[0].setOptions(oYAxisOptions);
                    oChart.yAxis[0].setExtremes(this.oChartVM.axisYMinValue,this.oChartVM.axisYMaxValue);
                }

                // OTHER Y AXIS
                if (!bIsStockChart)
                {

                    var iAxis = 0;

                    // Add Additional Axes
                    for (iAxis=0; iAxis<this.oChartVM.verticalAxes.length; iAxis++) {

                        var oAdditionalAxes = this.oChartVM.verticalAxes[iAxis];

                        oChart.addAxis({
                            title: {
                                text:oAdditionalAxes.axisYTitle,
                                rotation: 270,
                                margin: 30
                            },
                            opposite: oAdditionalAxes.isOpposite,
                            min: oAdditionalAxes.axisYMinValue,
                            max: oAdditionalAxes.axisYMaxValue,
                            tickInterval: oAdditionalAxes.axisYTickInterval,
                            minPadding: 0,
                            startOnTick: true
                        });

                        oChart.yAxis[iAxis+1].setExtremes(oAdditionalAxes.axisYMinValue,oAdditionalAxes.axisYMaxValue);
                    }
/*
                    // Add Additional Axes
                    this.oChartVM.verticalAxes.forEach(function(oAdditionalAxes) {
                        oChart.addAxis({
                            title: {
                                text:oAdditionalAxes.axisYTitle,
                                rotation: 270,
                                margin: 30
                            },
                            opposite: oAdditionalAxes.isOpposite,
                            min: oAdditionalAxes.axisYMinValue,
                            max: oAdditionalAxes.axisYMaxValue,
                            tickInterval: oAdditionalAxes.axisYTickInterval,
                            minPadding: 0,
                            startOnTick: true
                        });
                    });
*/
                }

                // X AXIS
                if (angular.isDefined(oChart.xAxis[0])) {
                    var oXAxisOptions = {
                        type: 'datetime'
                    };

                    oChart.xAxis[0].setOptions(oXAxisOptions);
                }

                // For each time Serie
                this.oChartVM.dataSeries.forEach(function(oSerie) {

                    // Check if exists
                    if (oSerie==null) return;
                    if (oSerie.data==null) return;

                    if (oSerie.axisId != 0) oSerie.yAxis = oSerie.axisId;

                    if (bIsStockChart)
                    {
                        // I need at least two points
                        if (oSerie.data.length>1)
                        {
                            // Get the two last elements
                            var oSecondToLastElement = oSerie.data[oSerie.data.length-2];
                            var oLastElement = oSerie.data[oSerie.data.length-1];
                            // Obtain time step
                            var iTimeDelta = oLastElement[0]-oSecondToLastElement[0];

                            // Get N hours offset
                            var iHoursOffset = 21600000 * 2;

                            // Compute max steps
                            var iMaxSteps = iHoursOffset/iTimeDelta;

                            // Add null values
                            for (var iSteps = 1; iSteps<=iMaxSteps; iSteps ++)
                            {
                                var oNullValue = [];

                                oNullValue[0] = oLastElement[0] + iSteps*iTimeDelta;
                                oNullValue[1] = null;

                                oSerie.data.push(oNullValue);
                            }
                        }
                    }


                    if (oSerie.name=="Raffica del Vento") {
                        // I need at least two points
                        if (oSerie.data.length>1) {
                            // Get the two last elements
                            var oSecondToLastElement = oSerie.data[oSerie.data.length - 2];
                            var oLastElement = oSerie.data[oSerie.data.length - 1];
                            // Obtain time step
                            var iTimeStart = oLastElement[0] - 3*60*60*24*1000;

                            if (oSerie.data[0][0]<iTimeStart)  {
                                oChart.xAxis[0].zoom(iTimeStart,oLastElement[0]);
                                if( !oChart.resetZoomButton ) {
                                    oChart.showResetZoom();
                                }
                            }
                        }

                    }

                    oChart.addSeries(oSerie);
                });
            }



        }
    }

    ChartController.prototype.zoomIn = function() {
        //var oDialog = this.m_oDialogService.getExistingDialog(this.m_sStationCode);
        this.m_iHeight *= 2;
        this.m_iWidth *= 2;
        this.addSeriesToChart();
    }

    ChartController.prototype.zoomOut = function() {
        //alert('out');

        this.m_iHeight /= 2;
        this.m_iWidth /= 2;
        this.addSeriesToChart();
    }

    ChartController.prototype.getHeight = function() {
        return this.m_iHeight + "px";
    }

    ChartController.prototype.getMinWidth = function() {
        return "310px";
    }

    ChartController.prototype.getWidth = function() {
        return this.m_iWidth + "px";
    }

    ChartController.$inject = [
        '$scope',
        'dialogService',
        'ChartService',
        '$timeout',
        'ConstantsService',
        '$log'
    ];
    return ChartController;
}) ();

