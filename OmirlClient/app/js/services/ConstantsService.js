/**
 * Created by p.campanella on 27/01/14.
 */

'use strict';
angular.module('omirl.ConstantsService', []).
    service('ConstantsService', [function () {
        //this.APIURL = 'http://localhost:8080/Omirl/rest';
        //this.APIURL = 'http://omirltest.regione.liguria.it/Omirl/rest';
        //this.APIURL = 'http://192.168.25.10:8080/Omirl/rest';
        this.URL = 'http://93.62.155.217:8080/Omirl/';
        this.APIURL = this.URL + '/rest';
        //this.WMSURL = 'http://93.62.155.217:8080/geoserver/wms';
        this.WMSURL = 'http://omirltest.regione.liguria.it/geoserver/wms';

        this.m_aoSensorLinks = [];
        this.m_aoHydroLinks = [];
        this.m_aoStaticLinks = [];
        this.m_iRefreshRateMs = 1000*60;
        this.m_bUserLogged = false;
        this.m_oUser = null;
        this.m_sReferenceDate = "";

        this.m_oMapCenter = null;
        this.m_oMapZoom = null;

        this.m_bJustLogged = false;

        this.m_sSensorLayerActive = null;
        this.m_bIsMiniVersion=false;

        this.m_aoFlattedHydroLinks = [];


        this.isMobile = function() {

            if (navigator.userAgent.match((/Android/i)) ||
                navigator.userAgent.match(/BlackBerry/i) ||
                navigator.userAgent.match(/iPhone|iPad|iPod/i) ||
                navigator.userAgent.match(/Opera Mini/i) ||
                navigator.userAgent.match(/IEMobile/i)
                )
                return true;

            return false;
        }

        this.isNowMode = function() {
            if (this.getReferenceDate() != null)
            {
                if (this.getReferenceDate() != "")
                {
                    return false;
                }
            }

            return true;
        }


        this.setSensorLayerActive = function(oCode) {
            this.m_sSensorLayerActive = oCode;
        }

        this.getSensorLayerActive = function() {
            return this.m_sSensorLayerActive;
        }

        this.setJustLogged = function(oVal) {
            this.m_bJustLogged = oVal;
        }

        this.getJustLogged = function() {
            return this.m_bJustLogged;
        }

        this.setIsMiniVersion = function(oVal) {
            this.m_bIsMiniVersion = oVal;
        }

        this.getIsMiniVersion = function() {
            return this.m_bIsMiniVersion;
        }

        this.getMapCenter = function() {
            return this.m_oMapCenter;
        }

        this.getMapZoom = function() {
            return this.m_oMapZoom;
        }

        this.setMapCenter = function(oCenter) {
            this.m_oMapCenter = oCenter;
        }

        this.setMapZoom = function(oZoom) {
            this.m_oMapZoom = oZoom;
        }

        this.getURL = function() {
            return this.URL;
        }

        this.getAPIURL = function() {
            return this.APIURL;
        }

        this.getWMSURL = function() {
            return this.WMSURL;
        }

        this.clearSensorLinks = function() {
            this.m_aoSensorLinks = [];
        }

        this.pushToSensorLinks = function(oSensorLink) {
            this.m_aoSensorLinks.push(oSensorLink);
        }

        this.getSensorLinkByType = function(sType) {

            for (var iElement = 0; iElement < this.m_aoSensorLinks.length; iElement++) {
                if (this.m_aoSensorLinks[iElement].code == sType) {
                    return this.m_aoSensorLinks[iElement];
                    break;
                }
            }

            return null;
        }

        this.getSensorsLinks = function() {
            return this.m_aoSensorLinks;
        }

        this.clearHydroLinks = function() {
            this.m_aoHydroLinks = [];
        }

        this.pushToHydroLinks = function(oHydroLink) {
            this.m_aoHydroLinks.push(oHydroLink);
        }

        this.getHydroLinkByType = function(sType) {

            for (var iElement = 0; iElement < this.m_aoHydroLinks.length; iElement++) {
                if (this.m_aoHydroLinks[iElement].linkCode == sType) {
                    return this.m_aoHydroLinks[iElement];
                    break;
                }
            }

            return null;
        }

        this.getHydroLinks = function() {
            return this.m_aoHydroLinks;
        }

        this.clearFlattedHydroLinks = function() {
            this.m_aoFlattedHydroLinks = [];
        }

        this.setFlattedHydroLinks = function(aoHydroLinks) {
            this.m_aoFlattedHydroLinks = aoHydroLinks;
        }

        this.getFlattedHydroLinkByType = function(sType) {

            for (var iElement = 0; iElement < this.m_aoFlattedHydroLinks.length; iElement++) {
                if (this.m_aoFlattedHydroLinks[iElement].linkCode == sType) {
                    return this.m_aoFlattedHydroLinks[iElement];
                    break;
                }
            }

            return null;
        }

        this.getFlattedHydroLinks = function() {
            return this.m_aoFlattedHydroLinks;
        }

        this.clearStaticLinks = function() {
            this.m_aoStaticLinks = [];
        }

        this.pushToStaticLinks = function(oSensorLink) {
            this.m_aoStaticLinks.push(oSensorLink);
        }

        this.getStaticLinkById = function(layerID) {

            for (var iElement = 0; iElement < this.m_aoStaticLinks.length; iElement++) {
                if (this.m_aoStaticLinks[iElement].layerID == layerID) {
                    return this.m_aoStaticLinks[iElement];
                    break;
                }
            }

            return null;
        }

        this.getStaticLinks = function(oSensorLink) {
            return this.m_aoStaticLinks;
        }


        this.getRefreshRateMs = function() {
            return this.m_iRefreshRateMs;
        }

        this.isUserLogged = function() {
            return this.m_bUserLogged;
        }

        this.isUserAdministrator = function() {
            var ret = false;
            if (this.isUserLogged()) {
                if (this.m_oUser != null) {
                    if (this.m_oUser.role == 1)
                        ret = true;
                }
            }
            return ret;
        }

        this.setUser = function(oUser) {

            this.m_bUserLogged = false;

            this.m_oUser = oUser;
            if (oUser != null)
            {
                if (oUser.isLogged == true)
                {
                    this.m_bUserLogged = true;
                    this.m_bJustLogged = true;
                }
                else
                {
                    this.m_bJustLogged = false;
                }
            }
            else
            {
                this.m_bJustLogged = false;
            }
        }

        this.getUser = function() {
            return this.m_oUser;
        }

        this.getSessionId = function() {
            if (this.m_oUser != null)
            {
                if (angular.isDefined(this.m_oUser.sessionId)) return this.m_oUser.sessionId;
            }

            return "";
        }

        this.setReferenceDate = function(sDate) {
            this.m_sReferenceDate = sDate;
        }

        this.getReferenceDate = function() {
            if (this.m_sReferenceDate !=null) return this.m_sReferenceDate;
            else return "";
        }

        this.pad = function (number, length){
            var str = "" + number;
            while (str.length < length) {
                str = '0'+str;
            }
            return str;
        }

        this.getTimezoneOffset  = function () {
            var offset = new Date().getTimezoneOffset()
            offset = ((offset<0? '+':'-')+ // Note the reversed sign!
                this.pad(parseInt(Math.abs(offset/60)), 2)+
                this.pad(Math.abs(offset%60), 2));

            return offset;
        }

        this.getReferenceDateString = function()
        {
            if (this.m_sReferenceDate == null) return "";
            if (this.m_sReferenceDate == "") return "";


            var oYear = this.m_sReferenceDate.getFullYear();
            var oMonth = this.m_sReferenceDate.getMonth() + 1;
            var oDay = this.m_sReferenceDate.getDate();
            var oHour = this.m_sReferenceDate.getHours();
            var oMin = this.m_sReferenceDate.getMinutes();

            if (oMonth<10)
            {
                oMonth = "0"+oMonth;
            }

            if (oDay<10)
            {
                oDay = "0"+oDay;
            }

            if (oHour<10)
            {
                oHour = "0"+oHour;
            }

            if (oMin<10)
            {
                oMin = "0"+oMin;
            }

            var sDateString = oYear+"-"+oMonth+"-" + oDay + 'T' + oHour + ':' + oMin + ':00.000' + this.getTimezoneOffset();

            return sDateString;
        }
    }]);