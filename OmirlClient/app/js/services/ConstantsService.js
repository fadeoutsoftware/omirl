/**
 * Created by p.campanella on 27/01/14.
 */

'use strict';
angular.module('omirl.ConstantsService', []).
    service('ConstantsService', [function () {
        //this.APIURL = 'http://localhost:8080/Omirl/rest';
        //this.APIURL = 'http://192.168.25.10:8080/Omirl/rest';
        this.APIURL = 'http://93.62.155.217:8080/Omirl/rest';

        this.m_aoSensorLinks = [];
        this.m_aoStaticLinks = [];
        this.m_iRefreshRateMs = 1000*60;
        this.m_bUserLogged = false;
        this.m_oUser = null;
        this.m_sReferenceDate = ""; // Diventerà tipo AAAAMMDDhhmm

        this.getAPIURL = function() {
            return this.APIURL;
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

        this.setUser = function(oUser) {

            this.m_bUserLogged = false;

            this.m_oUser = oUser;
            if (oUser != null)
            {
                if (oUser.isLogged == true)
                {
                    this.m_bUserLogged = true;
                }
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

    }]);