/**
 * Created by p.campanella on 30/05/2014.
 */

'use strict';

angular.module('omirl.chartService', ['omirl.ConstantsService']).
    service('ChartService', ['$http', 'ConstantsService',  function ($http, oConstantsService) {

        this.m_oHttp = $http;
        this.m_oConstantsService = oConstantsService;

        this.m_aoCharts = [];

        this.setChart  = function(sCode, oChart) {

            for (var iCount = 0; iCount<this.m_aoCharts.length; iCount++) {
                var oChartReference = this.m_aoCharts[iCount];
                if (angular.isDefined(oChartReference)) {
                    if (oChartReference.sCode == sCode) {
                        oChartReference.oChart = oChart;
                        break;
                    }
                }
            }
        }

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
            if ( sChart == 'Vento'){
	    	sChart = 'Vento2';
            }else {
	     if ( sChart == 'Vento2'){
	         sChart = 'Vento';
             }
	    }
            var sAPIURL = this.m_oConstantsService.getAPIURL();
            return this.m_oHttp.get(sAPIURL + '/charts/'+sSensorCode+'/'+sChart);
        }

        this.getSectionChart = function(sSectionCode, sModel, sSubFolder) {
            var sAPIURL = this.m_oConstantsService.getAPIURL();
            return this.m_oHttp.get(sAPIURL + '/charts/sections/'+ sSectionCode+'/'+ sModel);
        }

        this.exportCsvStationChart = function(sSensorCode, sChart) {
            var sAPIURL = this.m_oConstantsService.getAPIURL();
            //return this.m_oHttp.get(sAPIURL + '/charts/csv/'+sSensorCode+'/'+sChart);
            return sAPIURL + '/charts/csv/'+sSensorCode+'/'+sChart;
        }

        this.isStockChart = function(sSensorType) {
            if (sSensorType == 'Pluvio') return false;
            if (sSensorType == 'PluvioNative') return false;
            if (sSensorType == 'Pluvio30') return false;
            if (sSensorType == 'Pluvio7') return false;
            if (sSensorType == 'Vento') return false;
            if (sSensorType == 'Vento2') return false;

            return true;
        }


    }]);

