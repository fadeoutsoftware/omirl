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
            return this.m_oHttp.get(this.APIURL + '/stations/stationlist/'+sType);
        }

        this.getStationsTypes = function() {
            return this.m_oHttp.get(this.APIURL + '/stations/types');
        }

        this.exportCsvStationList = function(sSensorCode) {
            var sAPIURL = this.APIURL;
            return sAPIURL + '/stations/exportlist/'+sSensorCode;
        }

    }]);

