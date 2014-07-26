/**
 * Created by p.campanella on 30/01/14.
 */

'use strict';
angular.module('omirl.stationsService', []).
    service('StationsService', ['$http',  function ($http) {
        //this.APIURL = 'http://localhost:8080/Omirl/rest';
//        this.APIURL = 'http://192.168.25.10:8080/Omirl/rest';
        this.APIURL = 'http://93.62.155.217:8080/Omirl/rest';

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

    }]);

