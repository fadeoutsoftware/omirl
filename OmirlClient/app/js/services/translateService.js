/**
 * Created by s.adamo on 22/07/2015.
 */

/**
 * Created by p.campanella on 20/08/2014.
 */


'use strict';
angular.module('omirl.translateService', []).
    service('TranslateService', ['$translate', function ($translate) {

        this.m_oTranslate = $translate;
        this.DAEMON_PIOGGIA5MYAXIS;
        this.DAEMON_CUMULATAYAXIS;
        this.DAEMON_PIOGGIA10MYAXIS;
        this.DAEMON_PIOGGIA15MYAXIS;
        this.DAEMON_PIOGGIA30MYAXIS;
        this.DAEMON_PIOGGIAORAMYAXIS;
        this.DAEMON_TEMPERATURAYAXIS;
        this.DAEMON_LIVIDROYAXIS;
        this.DAEMON_VELOCITAVENTOYAXIS;
        this.DAEMON_RAFFICAYAXIS;
        this.DAEMON_VELOCITAVENTO2G;
        this.DAEMON_RAFFICA2GYAXIS;
        this.DAEMON_UMIDITAYAXIS;
        this.DAEMON_RADIAZIONEYAXIS;
        this.DAEMON_SUNSHINEDURATIONYAXIS;
        this.DAEMON_BAGNATURAYAXIS;
        this.DAEMON_PRESSIONEYAXIS;
        this.DAEMON_TENSIONEYAXIS;
        this.DAEMON_LUNGHEZZAMEDIAONDAYAXIS;
        this.DAEMON_ALTEZZANEVEYAXIS;
        this.DAEMON_PIOGGIA30GYAXIS;
        this.DAEMON_PIOGGIA7GYAXIS;

        this.loadTranslationsChart = function(language) {
            var oScope = this;

            this.m_oTranslate('DAEMON_PIOGGIAORAMYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIAORAMYAXIS = text;
            });

            this.m_oTranslate('DAEMON_CUMULATAYAXIS').then(function (text) {
                oScope.DAEMON_CUMULATAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA5MYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA5MYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA10MYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA10MYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA15MYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA15MYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA30MYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA30MYAXIS = text;
            });

            this.m_oTranslate('DAEMON_TEMPERATURAYAXIS').then(function (text) {
                oScope.DAEMON_TEMPERATURAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_LIVIDROYAXIS').then(function (text) {
                oScope.DAEMON_LIVIDROYAXIS = text;
            });

            this.m_oTranslate('DAEMON_VELOCITAVENTOYAXIS').then(function (text) {
                oScope.DAEMON_VELOCITAVENTOYAXIS = text;
            });

            this.m_oTranslate('DAEMON_RAFFICAYAXIS').then(function (text) {
                oScope.DAEMON_RAFFICAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_VELOCITAVENTO2G').then(function (text) {
                oScope.DAEMON_VELOCITAVENTO2G = text;
            });

            this.m_oTranslate('DAEMON_RAFFICA2GYAXIS').then(function (text) {
                oScope.DAEMON_RAFFICA2GYAXIS = text;
            });

            this.m_oTranslate('DAEMON_UMIDITAYAXIS').then(function (text) {
                oScope.DAEMON_UMIDITAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_SUNSHINEDURATIONYAXIS').then(function (text) {
                oScope.DAEMON_SUNSHINEDURATIONYAXIS = text;
            });
            
	    this.m_oTranslate('DAEMON_RADIAZIONEYAXIS').then(function (text) {
                oScope.DAEMON_RADIAZIONEYAXIS = text;
            });

            this.m_oTranslate('DAEMON_BAGNATURAYAXIS').then(function (text) {
                oScope.DAEMON_BAGNATURAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PRESSIONEYAXIS').then(function (text) {
                oScope.DAEMON_PRESSIONEYAXIS = text;
            });

            this.m_oTranslate('DAEMON_TENSIONEYAXIS').then(function (text) {
                oScope.DAEMON_TENSIONEYAXIS = text;
            });

            this.m_oTranslate('DAEMON_LUNGHEZZAMEDIAONDAYAXIS').then(function (text) {
                oScope.DAEMON_LUNGHEZZAMEDIAONDAYAXIS = text;
            });

            this.m_oTranslate('DAEMON_ALTEZZANEVEYAXIS').then(function (text) {
                oScope.DAEMON_ALTEZZANEVEYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA30GYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA30GYAXIS = text;
            });

            this.m_oTranslate('DAEMON_PIOGGIA7GYAXIS').then(function (text) {
                oScope.DAEMON_PIOGGIA7GYAXIS = text;
            });
        }

        this.getTranslation = function(key){
            try {
                return eval('this.' + key);
            }
            catch(err) {
                return key;
            }
        }


    }]);

