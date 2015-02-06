/**
 * Created by p.campanella on 30/01/14.
 */

'use strict';
angular.module('omirl.stationsService', ['omirl.ConstantsService']).
    service('StationsService', ['$http',  'ConstantsService', function ($http, oConstantsService) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;

        this.m_aoSensorsTable = [
            {"name":"Alassio", "stationCode":"ALASS","municipality":"Alassio","district":"SV","area":"","basin":"","subBasin":"", "lastValue":"11.0","value1":"12.0","value2":"8.0"},
            {"name":"Albenga - Isolabella", "stationCode":"ISBLL","municipality":"Albenga","district":"SV","area":"","basin":"Neva","subBasin":"Neva","lastValue":"0.0","value1":"0.0","value2":"0.0"},
            {"name":"Molino Branca", "stationCode":"MOBRA","municipality":"Albenga","district":"SV","area":"","basin":"Centa","subBasin":"Centa","lastValue":"5.0","value1":"5.0","value2":"0.0"},
            {"name":"Alpe Gorreto", "stationCode":"AGORR","municipality":"Gorreto","district":"GE","area":"","basin":"Trebbia","subBasin":"Trebbia","lastValue":"7.0","value1":"7.5","value2":"0.0"},
            {"name":"Alpe Vobbia", "stationCode":"AVOBB","municipality":"Vobbia","district":"GE","area":"","basin":"Vobbia","subBasin":"Vobbia","lastValue":"13.3","value1":"13.3","value2":"11.2"},
            {"name":"Barbagelata", "stationCode":"BRGEL","municipality":"Lorsica","district":"GE","area":"","basin":"Trebbia","subBasin":"Trebbia","lastValue":"2.0","value1":"5.0","value2":"0.0"},
            {"name":"Bestagno", "stationCode":"BESTA","municipality":"Pontedassio","district":"IM","area":"","basin":"Impero","subBasin":"impero","lastValue":"2.0","value1":"4.3","value2":"0.5"},
            {"name":"Brugnato", "stationCode":"BVARA","municipality":"Brugnato","district":"SP","area":"","basin":"Vara","subBasin":"Vara","lastValue":"3.6","value1":"4.1","value2":"1.9"}
        ];

        this.m_aoAggregationTypes = [
            { "description" : "Aree Allertamento", "code" : "AAL"},
            { "description" : "Comuni", "code" : "COM"},
            { "description" : "Province", "code" : "PRO"},
            { "description" : "Bacini", "code" : "BAC"}
        ];

        this.m_aoMaxTableRows = [
            {"name":"A","m5BkColor":"max-table-green-cell max-table-border-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"<b>6 mm</b> [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"<b>15 mm</b> [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"<b>18 mm</b> [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"<b>23 mm</b> [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"<b>29 mm</b> [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"<b>32 mm</b> [14:20] @ Centro Funzionale"},
            {"name":"B","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell max-table-border-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"C","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell max-table-border-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"C+","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell max-table-border-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"C-","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell max-table-border-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"Magra","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell max-table-border-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"D","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell max-table-border-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"E","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"Genova","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"3 mm [15:30] @ Centro Funzionale", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Centro Funzionale", "h1BkColor":"max-table-green-cell","h1":"15 mm [14:20] @ Centro Funzionale","h3BkColor":"max-table-green-cell", "h3":"18 mm [14:20] @ Centro Funzionale", "h6BkColor":"max-table-green-cell", "h6":"23 mm [14:20] @ Centro Funzionale","h12BkColor":"max-table-green-cell", "h12":"29 mm [14:20] @ Centro Funzionale","h24BkColor":"max-table-green-cell", "h24":"32 mm [14:20] @ Centro Funzionale"},
            {"name":"Savona","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"1 mm [14:20] @ Mallare, Murialdo", "m30BkColor":"max-table-green-cell", "m30":"9 mm [14:20] @ Cairo Montenotte", "h1BkColor":"max-table-yellow-cell","h1":"36 mm [14:20] @ Il Pero","h3BkColor":"max-table-red-cell", "h3":"36 mm [14:20] @ Calice Ligure - Ca rosse", "h6BkColor":"max-table-green-cell", "h6":"36 mm [14:20] @ Sanda","h12BkColor":"max-table-green-cell", "h12":"36 mm [14:20] @ Sassello","h24BkColor":"max-table-green-cell", "h24":"36 mm [14:20] @ Colle del Melogno"},
            {"name":"Imperia","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"0 mm [14:20] @ Triora", "m30BkColor":"max-table-green-cell", "m30":"6 mm [14:20] @ Borgomaro, Bestagno", "h1BkColor":"max-table-green-cell","h1":"14 mm [14:20] @ Sella di Gouta","h3BkColor":"max-table-yellow-cell", "h3":"15 mm [14:20] @ Colle di Nava", "h6BkColor":"max-table-green-cell ", "h6":"16 mm [14:20] @ Ventimiglia","h12BkColor":"max-table-green-cell", "h12":"16 mm [14:20] @ Ranzo","h24BkColor":"max-table-green-cell max-table-border-cell", "h24":"19 mm [14:20] @ Dolcedo, Pornassio"},
            {"name":"La Spezia","m5BkColor":"max-table-green-cell", "m5":"<b>3 mm</b> [15:30] @ Centro Funzionale", "m15BkColor":"max-table-green-cell", "m15":"4 mm [14:20] @ Monterosso, La Spezia", "m30BkColor":"max-table-green-cell", "m30":"4 mm [14:20] @ Monte Rocchetta", "h1BkColor":"max-table-green-cell","h1":"6 mm [14:20] @ Piana Battolla - Ponte","h3BkColor":"max-table-green-cell", "h3":"16 mm [14:20] @ Brugnato", "h6BkColor":"max-table-green-cell", "h6":"22 mm [14:20] @ La Spezia","h12BkColor":"max-table-green-cell", "h12":"26 mm [14:20] @ Monte Rocchetta","h24BkColor":"max-table-green-cell", "h24":"27 mm [14:20] @ La Macchia, Sarzana - Nave"}
        ]

        this.getStations = function(oStationsLink) {
            return this.m_oHttp.get(this.APIURL + '/stations/'+oStationsLink.code);
        }

        this.getStationsOLD = function(oStationsLink) {
            var aoSensors = [
                {"stationId": 1, "name":"Molino Branca","lat":44.049168,"lon":8.212778,"value":1,"refDate":"/Date(1391542388310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"},
                {"stationId": 2, "name":"Sesta Godano","lat":44.298332,"lon":9.6775,"value":1,"refDate":"/Date(1391542388310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"},
                {"stationId": 1, "name":"Quiliano","lat":44.291,"lon":8.414,"value":1,"refDate":"/Date(1391542478310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"}
            ];

            var sLayerType = oStationsLink.code;

            aoSensors.forEach(function(oEntry) {
                oEntry.value = Math.round(Math.random()*410);
            });
            return aoSensors;
        }

        this.getSensorsTable = function(oStationsLink) {
            //var sLayerType = oStationsLink.code;
            return this.m_aoSensorsTable;
        }


        this.getWeather = function() {
            var aoSensors = [
                {"stationId": 1, "name":"Molino Branca","lat":44.049168,"lon":8.212778,"value":1,"refDate":"/Date(1391542388310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"},
                {"stationId": 2, "name":"Sesta Godano","lat":44.298332,"lon":9.6775,"value":1,"refDate":"/Date(1391542388310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"},
                {"stationId": 1, "name":"Ferrea","lat":44.29194,"lon":8.369123,"value":1,"refDate":"/Date(1391542478310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"},
                {"stationId": 1, "name":"Quiliano","lat":44.291,"lon":8.414,"value":1,"refDate":"/Date(1391542478310)/","shortCode":"STNID","alt":"120m","otherHtml":"","imgPath":"img/marker.png"}
            ];


            aoSensors.forEach(function(oEntry) {
                oEntry.value = Math.round(Math.random()*31+1);

                var iIndex = Math.round(Math.random()*31+1);
                oEntry.imgPath = "img/weather/w"+iIndex+".png";
            });
            return aoSensors;
        }

        this.getStationsTable = function(sType) {
            return this.m_oHttp.get(this.APIURL + '/stations/stationlist/'+sType);
        }

        this.getStationsTypes = function() {
            return this.m_oHttp.get(this.APIURL + '/stations/types');
        }

        this.exportCsvStationList = function(sSensorCode) {
            var sAPIURL = this.APIURL;
            return sAPIURL + '/stations/exportlist/'+sSensorCode;
        }

        this.getAggregationsTypes = function() {
            return this.m_aoAggregationTypes;
        }

        this.getMaxStationsTable = function(sAggregation) {
            return this.m_aoMaxTableRows;
        }

    }]);

