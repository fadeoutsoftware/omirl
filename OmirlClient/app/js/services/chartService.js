/**
 * Created by p.campanella on 30/05/2014.
 */

'use strict';
angular.module('omirl.chartService', []).
    service('ChartService', ['$http',  function ($http) {
        //this.APIURL = 'http://localhost:8080/Omirl/rest';
        //this.APIURL = 'http://192.168.25.10:8080/Omirl/rest';
        this.APIURL = 'http://93.62.155.217:8080/Omirl/rest';

        this.m_oHttp = $http;

        this.m_aoCharts = [];

        //this.getStations = function(oStationsLink) {
        //    return this.m_oHttp.get(this.APIURL + '/stations/'+oStationsLink.code);
        //}

        this.addChart  = function(sCode, oChart) {
            var oChartReference = {};

            oChartReference.sCode = sCode;
            oChartReference.oChart = oChart;

            this.m_aoCharts.push(oChartReference);
        }

        this.getChart = function(sCode) {
            for (var iCount = 0; iCount<this.m_aoCharts.length; iCount++) {
                var oChartReference = this.m_aoCharts[iCount];
                if (angular.isDefined(oChartReference)) {
                    if (oChartReference.sCode == sCode) {
                        return oChartReference.oChart;
                    }
                }
            }

            return null;
        }

        this.removeChart = function(sCode) {
            for (var iCount = 0; iCount<this.m_aoCharts.length; iCount++) {
                var oChartReference = this.m_aoCharts[iCount];
                if (angular.isDefined(oChartReference)) {
                    if (oChartReference.sCode == sCode) {
                        this.m_aoCharts.splice(iCount,1);
                        return;
                    }
                }
            }
        }

        this.getStationChart = function(sSensorCode, sChart) {
            return this.m_oHttp.get(this.APIURL + '/charts/'+sSensorCode+'/'+sChart);
        }

        this.getStationChartOLD = function(sSensorCode, sVariable) {

            var oChartViewModel = {
                "title": sSensorCode,
                "subtitle": sVariable,
                "chartSeries": [
                    {
                        "name": "Prova 1",
                        "type": "line",
                        "data": [[1364774400000,4],[1364860800000,3],[1364947200000,5],[1365033600000,8],[1365120000000,4],[1365379200000,6],[1365465600000,7],[1365552000000,8],[1365638400000,2],[1365724800000,4],[1365984000000,5],[1366070400000,6]
                            ,[1366156800000,7],[1366243200000,3],[1366329600000,8],[1366588800000,2],[1366675200000,0],[1366761600000,6],[1366848000000,2],[1366934400000,4],[1367193600000,1],[1367280000000,0],[1367366400000,9],[1367452800000,8]
                            ,[1367539200000,4],[1367798400000,2],[1367884800000,7],[1367971200000,5],[1368057600000,3],[1368144000000,0]]
                    },
                    {
                        "name": "Prova 2",
                        "type": "spline",
                        "data": [[1364774400000,6],[1364860800000,7],[1364947200000,2],[1365033600000,5],[1365120000000,0],[1365379200000,2],[1365465600000,7],[1365552000000,8],[1365638400000,2],[1365724800000,4],[1365984000000,5],[1366070400000,6]
                            ,[1366156800000,7],[1366243200000,3],[1366329600000,3],[1366588800000,5],[1366675200000,9],[1366761600000,4],[1366848000000,2],[1366934400000,4],[1367193600000,1],[1367280000000,0],[1367366400000,9],[1367452800000,8]
                            ,[1367539200000,4],[1367798400000,2],[1367884800000,5],[1367971200000,5],[1368057600000,3],[1368144000000,0]]                    },
                    {
                        "name": "Prova 3",
                        "type": "column",
                        "data": [[1364774400000,7],[1364860800000,8],[1364947200000,1],[1365033600000,5],[1365120000000,7],[1365379200000,0],[1365465600000,7],[1365552000000,8],[1365638400000,2],[1365724800000,4],[1365984000000,5],[1366070400000,6]
                            ,[1366156800000,2],[1366243200000,4],[1366329600000,2],[1366588800000,7],[1366675200000,0],[1366761600000,6],[1366848000000,3],[1366934400000,4],[1367193600000,1],[1367280000000,0],[1367366400000,9],[1367452800000,8]
                            ,[1367539200000,8],[1367798400000,5],[1367884800000,4],[1367971200000,5],[1368057600000,7],[1368144000000,1]]                    }
                ]


            }

            return oChartViewModel;

        }

    }]);

