/**
 * Created by p.campanella on 30/01/14.
 */

'use strict';
angular.module('omirl.stationsService', ['omirl.ConstantsService']).
    service('StationsService', ['$http',  'ConstantsService', function ($http, oConstantsService) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;

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
            var aoStations = [
                {"stationCode": "LERCA", "name":"Molino Branca","district":"Savona","basin":"Letimbro","network":"ETG"},
                {"stationCode": "PIAMP", "name":"Pornassio","district":"Imperia","basin":"Imperia","network":"MTX"},
                {"stationCode": "MTPOR", "name":"GE Pegli","district":"Genova","basin":"Bisagno","network":"ETG"},
                {"stationCode": "ARNAL", "name":"Mele","district":"Genova","basin":"Bisagno","network":"ETG"},
                {"stationCode": "CFUNZ", "name":"La Presa","district":"Genova","basin":"Bisagno","network":"ETG"},
                {"stationCode": "RIGHI", "name":"Bolzaneto","district":"Genova","basin":"Polcevera","network":"ETG"},
                {"stationCode": "CLARI", "name":"Gavette","district":"Genova","basin":"Bisagno","network":"ETG"},
                {"stationCode": "INASV", "name":"Isoverde","district":"Genova","basin":"Bisagno","network":"ETG"},
                {"stationCode": "PEROO", "name":"Fiorino","district":"Genova","basin":"Polcevera","network":"MTX"},
                {"stationCode": "SANDA", "name":"Levanto","district":"La Spezia","basin":"Magra","network":"ETG"},
                {"stationCode": "IMPER", "name":"Soliera","district":"La Spezia","basin":"Magra","network":"MTX"},
                {"stationCode": "BESTA", "name":"Lagdei","district":"La Spezia","basin":"Magra","network":"CAE"}
            ];

            return aoStations;
        }

        this.getStationsTypes = function() {
            var aoTypes = [
                {"description": "Pluviometri", "code":"Pluvio"},
                {"description": "Termometri", "code":"Termo"},
                {"description": "Idrometri", "code":"Idro"},
                {"description": "Anemometri", "code":"Vento"},
                {"description": "Igrometri", "code":"Igro"},
                {"description": "Radiazione", "code":"Radio"}
            ];

            return aoTypes;
        }

    }]);

