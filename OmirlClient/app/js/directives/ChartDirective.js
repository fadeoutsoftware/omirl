/**
 * Created by p.campanella on 02/06/2014.
 */


/**
 * Created by p.campanella on 30/05/2014.
 */

'use strict';
angular.module('omirl.chartDirective', []).
    directive('omirlHighChart', ['ChartService',  function (oChartService) {

        var m_oChartService = oChartService;



        return {
            restrict: 'EA',
            priority: -10,
            link: function (scope, elem, attrs) {

                var oChartOptions;

                if (scope.m_oController.m_oDialogModel.isStock) {
                    var oStockOptions = {
                        chart: {
                            renderTo: elem[0]
                        },
                        credits: {
                            enabled: false
                        },
                        plotOptions: {
                            series: {
                                dataGrouping: {
                                    enabled: true
                                },
                                animation: false
                            },
                            line: {
                                marker: {
                                    enabled: false
                                }
                            },
                            spline: {
                                marker: {
                                    enabled: false
                                }
                            }
                        },
                        rangeSelector: {
                            inputEnabled: true,
                            selected: 4,
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
                            }, {
                                type: 'day',
                                count: 10,
                                text: '10gg'
                            }, {
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
                            valueDecimals: 2
                        },
                        exporting: {
                            buttons: {
                                contextButton: {
                                    symbol: 'url(img/chartdownload.png)'
                                }
                            }
                        }
                    };

                    oChartOptions = oStockOptions;
                }
                else {
                    var oSimpleChartOptions = {
                        chart: {
                            renderTo: elem[0],
                            zoomType: "xy"
                        },
                        credits: {
                            enabled: false
                        },
                        tooltip: {
                            pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b>',
                            valueDecimals: 2
                        },
                        plotOptions: {
                            line: {
                                marker: {
                                    enabled: false
                                }
                            },
                            spline: {
                                marker: {
                                    enabled: false
                                }
                            },
                            series: {
                                animation: false
                            }
                        },
                        xAxis: {
                            type: 'datetime'
                        },
                        title:{
                            text:''
                        },
                        exporting: {
                            buttons: {
                                contextButton: {
                                    symbol: 'url(img/chartdownload.png)'
                                }
                            }
                        }
                    };

                    oChartOptions = oSimpleChartOptions;
                }



                Highcharts.setOptions({
                    global: {
                        useUTC: false
                    }
                });

                Highcharts.setOptions({
                    lang: {
                        months: ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno',  'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'],
                        weekdays: ['Domenica', 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato'],
                        noData: ['Non ci sono dati disponibili'],
                        printChart: ['Stampa'],
                        shortMonths: ['Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu',  'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic'],
                        contextButtonTitle: ['Esporta Grafico'],
                        downloadJPEG: ['Scarica JPEG'],
                        downloadPDF: ['Scarica PDF'],
                        downloadPNG: ['Scarica PNG'],
                        downloadSVG: ['Scarica SVG'],
                        rangeSelectorFrom: ['Da'],
                        rangeSelectorTo: ['A']
                    }
                });

                var oChart =  new Highcharts.Chart(oChartOptions);

                m_oChartService.addChart(attrs.omirlHighChart, oChart);
            }
        };
    }]);

