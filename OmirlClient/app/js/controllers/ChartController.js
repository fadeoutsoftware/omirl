/**
 * Created by p.campanella on 30/05/2014.
 */


var ChartController = (function() {
    function ChartController($scope, dialogService, oChartService, $timeout, oConstantsService, $log, $translate, oTranslateService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oDialogService = dialogService;
        this.m_oChartService = oChartService;
        this.m_oConstantsService = oConstantsService;
        this.m_aoOtherCharts = [];
        this.m_bLoading = true;
        this.m_oLog = $log;
        this.m_oTranslate = $translate;
        this.m_oTranslationService = oTranslateService;
        this.m_oDialogModel = this.m_oScope.model;
        this.m_sDialogTitle = "";

        this.oChartVM = [];

        this.m_sSectionCode = this.m_oScope.model.stationCode;
        this.m_sChartType = this.m_oScope.model.chartType;

        this.m_iHeight = 490;
        this.m_iWidth = 730;

        var oControllerVar = this;
        this.m_sSubtitle = '';


        oControllerVar.m_oTranslate('CHARTCONTROLLER_OPTIONSUBTITLE', {model: this.m_oScope.model.name, municipality: this.m_oScope.model.municipality}).then(function(text){
            oControllerVar.m_sSubtitle = text;

        });

        oControllerVar.LoadData();

        Highcharts.SVGRenderer.prototype.symbols.windArrow = function(x, y, w, h){

            return [
                'M', 0, 7, // base of arrow
                'L', -1.5, 7,
                0, 10,
                1.5, 7,
                0, 7,
                0, -10 // top
            ];
        };

        if (Highcharts.VMLRenderer) {
            Highcharts.VMLRenderer.prototype.symbols.cross = Highcharts.SVGRenderer.prototype.symbols.cross;
        }

        //Translation
        /*
        this.DAEMON_PIOGGIAORAMYAXIS;
        this.DAEMON_CUMULATAYAXIS;
        oControllerVar.m_oTranslate('DAEMON_PIOGGIAORAMYAXIS').then(function (text) {
            oControllerVar.DAEMON_PIOGGIAORAMYAXIS = text;
        });

        oControllerVar.m_oTranslate('DAEMON_CUMULATAYAXIS').then(function (text) {
            oControllerVar.DAEMON_CUMULATAYAXIS = text;
        });*/

        this.m_oTranslationService.loadTranslationsChart();

    }




    ChartController.prototype.LoadData = function () {
        var oControllerVar = this;

        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart(this.m_sSectionCode,this.m_sChartType).success(function(data,status) {

            if (!angular.isDefined(data)){
                oControllerVar.m_oTranslate('CHARTCONTROLLER_LOADDATA', {value: oControllerVar.m_sSectionCode}).then(function(text){
                    alert(text);
                });

                oControllerVar.m_bLoading = false;
                return;
            }
            if (data=="") {

                oControllerVar.m_oTranslate('CHARTCONTROLLER_LOADDATA', {value: oControllerVar.m_sSectionCode}).then(function(text){
                    alert(text);
                });
                oControllerVar.m_bLoading = false;
                return;
            }

            oControllerVar.oChartVM = data;
            oControllerVar.m_aoOtherCharts = [];

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
            oControllerVar.m_oTranslate('ERRORCONTACTSERVER').then(function(error){
                oControllerVar.m_oLog.error(error);
            });

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

        oControllerVar.oChartVM = oControllerVar.m_oChartService.getStationChart(this.m_sSectionCode,oOtherLink.sensorType).success(function(data,status) {

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
            oControllerVar.m_oTranslate('ERRORCONTACTSERVER').then(function(error){
                oControllerVar.m_oLog.error(error);
            });
        });

    }

    ChartController.prototype.csvExport = function (sCode, sChart) {
        window.open(this.m_oChartService.exportCsvStationChart(this.m_sSectionCode,this.m_sChartType), '_blank', '');
    }

    ChartController.prototype.addSeriesToChart = function () {

        var oControllerVar = this;

        // Get Chart reference for this Chart
        var oChart = this.m_oChartService.getChart(this.m_sSectionCode);

        // Get From the model if it is a Stock or a Normal Chart
        var bIsStockChart = this.m_oScope.model.isStock;

        //Date now to show vertical line
        var timenow = new Date().getTime();

        if (this.m_oConstantsService.getReferenceDate()!="")
        {
            timenow = this.m_oConstantsService.getReferenceDate();
        }

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

                var bold = false;

                var oElement = oChart.options.chart.renderTo;

                // Add Columns settings
                if (bIsStockChart) {

                    oChartOptions = {
                        chart: {
                            renderTo: oElement,
                            zoomType: "xy",
                            width: this.m_iWidth,
                            height: this.m_iHeight

                        },
                        xAxis: {
                            type:'datetime',
                            labels: {
                                formatter: function () {
                                    var oDate = new Date(this.value);
                                    if (oDate.getHours() != 0 && oDate.getMinutes() == 0)
                                        return Highcharts.dateFormat('%H:%M', this.value);
                                    else
                                        return '<b>' + Highcharts.dateFormat('%d %b', this.value) + '</b>';
                                }
                            }
                        },
                        credits: {
                            enabled: false
                        },
                        subtitle:{
                            text:  oControllerVar.m_sSubtitle
                        },
                        plotOptions: {
                            series: {
                                dataGrouping: {
                                    enabled: true,
                                    approximation :"high"
                                },
                                animation: false
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
                            zoomType: "xy",
                            width: this.m_iWidth,
                            height: this.m_iHeight
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
                            type: 'datetime',
                            labels: {
                                formatter: function () {
                                    var oDate = new Date(this.value);
                                    if (oDate.getHours() != 0 && oDate.getMinutes() == 0)
                                        return Highcharts.dateFormat('%H:%M', this.value);
                                    else
                                        return '<b>' + Highcharts.dateFormat('%d %b', this.value) + '</b>';
                                }
                            }
                        },
                        title:{
                            text:''
                        },
                        subtitle:{
                            text: oControllerVar.m_sSubtitle
                        },
                        plotOptions: {
                            column: {
                                //pointRange: 1000*60*60,
                                //pointWidth: 5,
                                pointPlacement: -0.5,
                                pointPadding: 0,
                                grouping: false,
                                borderWidth: 0,
                                groupPadding: false
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
                            },
                            series: {
                                animation: false
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
                this.m_oChartService.setChart(this.m_sSectionCode,oChart);



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
                    var iMargin = 30;
                    var xLabel =  -15;
                    var yLabel = 3;

                    if (bIsStockChart) {
                        iRotation = 270;
                        iMargin = 10;
                        xLabel = 18;
                        yLabel = 5;
                    }




                    // Set Main Axis Options
                    //eval('oControllerVar.' + this.oChartVM.axisYTitle)
                    var oYAxisOptions = {
                        min: this.oChartVM.axisYMinValue,
                        max: this.oChartVM.axisYMaxValue,
                        tickInterval: this.oChartVM.axisYTickInterval,
                        title: {
                            text: oControllerVar.m_oTranslationService.getTranslation(this.oChartVM.axisYTitle) ,
                            rotation: iRotation,
                            margin: iMargin,
                            offset:40
                        },
                        opposite: this.oChartVM.axisIsOpposite,
                        plotLines: oPlotLines,
                        minPadding: 0,
                        startOnTick: true,
                        alternateGridColor: 'rgba(0, 144, 201, 0.1)',
                        labels:{
                            x: xLabel,
                            y: yLabel
                        }
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
                                text: oControllerVar.m_oTranslationService.getTranslation(oAdditionalAxes.axisYTitle),
                                rotation: 270,
                                margin: 20
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
                }

                var xAxisDefined = false;

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


                    if (oSerie.name=="Wind Direction") {

                        var oSerieCustomMarker = new Highcharts.Series();
                        oSerieCustomMarker.data = new Array();
                        oSerieCustomMarker.name = 'Wind Direction';
                        oSerieCustomMarker.axisId = oSerie.axisId;
                        oSerieCustomMarker.type = "scatter";
                        for(var iElement = 0; iElement< oSerie.data.length; iElement++)
                        {
                            // if direction not null
                            if (oSerie.data[iElement][1] != null) {

                                //var oData = {x: oSerie.data[iElement][0], y:100, marker:{symbol: 'windArrow'}};
                                var oData = {x: oSerie.data[iElement][0], y:100, marker:{symbol: 'url(img/windDirections/' + oSerie.data[iElement][1] + '.png)'}, toolText: oSerie.data[iElement][1]};
                                oSerieCustomMarker.data.push(oData);
                            }
                        }
                        // replace data serie
                        oSerie = oSerieCustomMarker;
                        //wind direction arrow
                        //oControllerVar.drawWindArrows(oChart, oSerie);

                        oSerie.tooltip = {
                            pointFormat: '<tr><td style="color: {series.color}">{series.name}: </td>' +
                            '<td style="text-align: right"><b>{point.toolText}</b></td></tr>'
                        }

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

                    if (!xAxisDefined) {
                        if (angular.isDefined(oChart.xAxis[0])) {
                            if (oControllerVar.oChartVM.dataSeries.length > 0) {
                                var oLastvalue = null;
                                //search last value != null
                                for (var iCount = oControllerVar.oChartVM.dataSeries[0].data.length - 1; iCount >= 0; iCount--) {
                                    if (oControllerVar.oChartVM.dataSeries[0].data[iCount][1] != null) {
                                        oLastvalue = oControllerVar.oChartVM.dataSeries[0].data[iCount][0];
                                        break;
                                    }
                                }
                                // X AXIS
                                if (angular.isDefined(oChart.xAxis[0])) {
                                    var oXAxisOptions = {
                                        type: 'datetime',
                                        gridLineWidth: 2,
                                        plotBands: [
                                            {
                                                color: 'rgba(255, 208, 0, 0.3)', // Color value
                                                from: timenow, // Start of the plot band
                                                to: oLastvalue, // End of the plot band
                                                zIndex: 4
                                            }
                                        ],
                                        plotLines: [
                                            {
                                                color: 'rgba(69, 163, 202, 1)', // Color value
                                                dashStyle: 'Solid', // Style of the plot line. Default to solid
                                                value: timenow, // Value of where the line will appear
                                                width: '2', // Width of the line
                                                zIndex: 4
                                            }
                                        ],
                                        tickPixelInterval: 50,
                                        minorTickInterval: 'auto'
                                    };
                                }
                            }
                            else {
                                var oXAxisOptions = {
                                    type: 'datetime'
                                };
                            }
                            xAxisDefined = true;
                            oChart.xAxis[0].setOptions(oXAxisOptions);
                        }
                    }

                    oChart.addSeries(oSerie);
                });


            }

        }
    }

    ChartController.prototype.zoomIn = function() {
        //var oDialog = this.m_oDialogService.getExistingDialog(this.m_sSectionCode);
        this.m_iHeight *= 1.1;
        this.m_iWidth *= 1.1;
        this.LoadData();
        this.addSeriesToChart();
    }

    ChartController.prototype.zoomOut = function() {
        //alert('out');

        this.m_iHeight /= 1.1;
        this.m_iWidth /= 1.1;
        this.LoadData();
        this.addSeriesToChart();
    }

    ChartController.prototype.getHeight = function() {
        return this.m_iHeight.toString() + "px";
    }

    ChartController.prototype.getMinWidth = function() {
        return "310px";
    }

    ChartController.prototype.getWidth = function() {
        return this.m_iWidth.toString() + "px";
    }

    ChartController.prototype.windArrow = function (name) {
        var level,
            path;


        // The stem and the arrow head
        path = [
            'M', 0, 7, // base of arrow
            'L', -1.5, 7,
            0, 10,
            1.5, 7,
            0, 7,
            0, -10 // top
        ];

        level = $.inArray(name, ['Calm', 'Light air', 'Light breeze', 'Gentle breeze', 'Moderate breeze',
            'Fresh breeze', 'Strong breeze', 'Near gale', 'Gale', 'Strong gale', 'Storm',
            'Violent storm', 'Hurricane']);

        if (level === 0) {
            path = [];
        }

        if (level === 2) {
            path.push('M', 0, -8, 'L', 4, -8); // short line
        } else if (level >= 3) {
            path.push(0, -10, 7, -10); // long line
        }

        if (level === 4) {
            path.push('M', 0, -7, 'L', 4, -7);
        } else if (level >= 5) {
            path.push('M', 0, -7, 'L', 7, -7);
        }

        if (level === 5) {
            path.push('M', 0, -4, 'L', 4, -4);
        } else if (level >= 6) {
            path.push('M', 0, -4, 'L', 7, -4);
        }

        if (level === 7) {
            path.push('M', 0, -1, 'L', 4, -1);
        } else if (level >= 8) {
            path.push('M', 0, -1, 'L', 7, -1);
        }

        return path;
    };

    ChartController.prototype.drawWindArrows = function (chart, serie) {
        var meteogram = this;

        $.each(chart.series[0].data, function (i, point) {
            var sprite, arrow, x, y;


            for (var iArrowCount = 0; iArrowCount < serie.data.length; iArrowCount++) {
                if (point.x == serie.data[iArrowCount][0]) {

                    var deg = serie.data[iArrowCount][1];
                    if (deg != null) {
                        // Draw the wind arrows
                        x = point.plotX + chart.plotLeft + 7;
                        y = 255;

                        arrow = chart.renderer.path(
                            meteogram.windArrow("Light air")
                        ).attr({
                                rotation: parseInt(deg, 10),
                                translateX: x, // rotation center
                                translateY: y // rotation center
                            });

                        arrow.attr({
                            stroke: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black',
                            'stroke-width': 1.5,
                            zIndex: 5
                        })
                            .add();
                    }

                }
            }
        });
    };

    ChartController.$inject = [
        '$scope',
        'dialogService',
        'ChartService',
        '$timeout',
        'ConstantsService',
        '$log',
        '$translate',
        'TranslateService'

    ];
    return ChartController;
}) ();

