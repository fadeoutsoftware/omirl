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

                var oChartOptions = {
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
                            count: 14,
                            text: '14gg'
                        }, {
                            type: 'all',
                            text: 'Tutti'
                        }],
                        inputDateFormat:'%d/%m/%Y',
                        inputEditDateFormat:'%d/%m/%Y'
                    },
                    tooltip: {
                        pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b>',
                        valueDecimals: 2
                    }
                };

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
                        contextButtonTitle: ['Opzioni Grafico'],
                        downloadJPEG: ['Scarica come JPEG'],
                        downloadPDF: ['Scarica come PDF'],
                        downloadPNG: ['Scarica come PNG'],
                        downloadSVG: ['Scarica come SVG']
                    }
                });

                var oChart =  new Highcharts.StockChart(oChartOptions);

                m_oChartService.addChart(attrs.omirlHighChart, oChart);
            }
        };
    }]);

