/**
 * Created by p.campanella on 05/11/2014.
 */

'use strict';
angular.module('omirl.TableService', ['omirl.ConstantsService']).
    service('TableService', ['$http',  'ConstantsService', '$location', '$translate', function ($http, oConstantsService, oLocation, $translate) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;
        this.m_oLocation = oLocation;
        this.m_bLinksFetched = false;
        this.m_oTranslate = $translate;
        this.m_sTableLegendSelected = "";
        this.m_sDataTableLegendSelected = "";


        this.m_aoTableLinks = [/*
            {
                "code": "StationValues",
                "description": "Valori Stazioni",
                "imageLinkOff": "img/tables/stationvalues.png",
                "isActive": false,
                "location": '/sensorstable'
            },
            {
                "code": "Max",
                "description": "Massimi Puntuali",
                "imageLinkOff": "img/tables/max.png",
                "isActive": false,
                "location": '/maxtable'
            },
            {
                "code": "Sintesi",
                "description": "Sintesi",
                "imageLinkOff": "img/tables/sintesi.png",
                "isActive": true,
                "location": '/summarytable'
            }*/];

        this.m_aoDataTableLinks = [/*
            {
                "code": "Stations",
                "description": "Tabella Stazioni",
                "imageLinkOff": "img/tables/max.png",
                "isActive": true,
                "location": '/stationstable'
            },
            {
                "code": "Models",
                "description": "Modelli Idrologici",
                "imageLinkOff": "img/tables/sintesi.png",
                "isActive": false,
                "location": '/modelstable'
            }*/
        ];

        this.refreshTableLinks = function() {
            this.m_bLinksFetched = false;
        }

        this.getDataTableLinks = function() {

            var oService = this;

            if (this.m_bLinksFetched) return this.m_aoDataTableLinks;
            else {

                this.m_oHttp.get(this.APIURL + '/tables/tablelinks').success(function(data,status) {
                    oService.m_aoTableLinks = data;
                }).error(function(data,status){
                    oService.m_oTranslate('ERRORCONTACTSERVER').then(function (error){
                        alert(error);
                    });
                });


                this.m_oHttp.get(this.APIURL + '/tables/datatablelinks').success(function(data,status) {
                    oService.m_aoDataTableLinks = data;
                }).error(function(data,status){
                    oService.m_oTranslate('ERRORCONTACTSERVER').then(function (error){
                        alert(error);
                    });
                });

                this.m_bLinksFetched = true;
            }
        }

        this.getTableLinks = function() {

            var oService = this;

            if (this.m_bLinksFetched) return this.m_aoTableLinks;
            else {

                this.m_oHttp.get(this.APIURL + '/tables/tablelinks').success(function(data,status) {
                    oService.m_aoTableLinks = data;

                    var iTableLinks;
                    for (iTableLinks=0; iTableLinks<data.length; iTableLinks++) {
                        if (data[iTableLinks].active) {
                            oService.m_sTableLegendSelected = data[iTableLinks].description;
                        }
                    }

                }).error(function(data,status){
                    oService.m_oTranslate('ERRORCONTACTSERVER').then(function (error){
                        alert(error);
                    });
                });


                this.m_oHttp.get(this.APIURL + '/tables/datatablelinks').success(function(data,status) {
                    oService.m_aoDataTableLinks = data;

                    var iTableLinks;
                    for (iTableLinks=0; iTableLinks<data.length; iTableLinks++) {
                        if (data[iTableLinks].active) {
                            oService.m_sDataTableLegendSelected = data[iTableLinks].description;
                        }
                    }

                }).error(function(data,status){
                    oService.m_oTranslate('ERRORCONTACTSERVER').then(function (error){
                        alert(error);
                    });
                });

                this.m_bLinksFetched = true;
            }

        }

        this.tableLinkClickedByLink = function (sLink) {

            var oTable =  null;

            var iTableLinks = 0;

            // Add Additional Axes
            for (iTableLinks=0; iTableLinks<this.m_aoTableLinks.length; iTableLinks++) {
                var oTempTableLink = this.m_aoTableLinks[iTableLinks];
                if (oTempTableLink != null) {
                    if (oTempTableLink.location == sLink) {
                        oTable = oTempTableLink;
                        break;
                    }
                }
            }

            if (oTable == null) return;

            // Check if the sensor link is active
            if (!oTable.isActive){
                // Set the textual description
                this.m_sTableLegendSelected = oTable.description;

                // Reset all actives flag
                this.m_aoTableLinks.forEach(function(oEntry) {
                    oEntry.isActive = false;
                });

                // Set this as the active one
                oTable.isActive = true;

                this.m_sTableLegendSelected = oTable.description;

                //this.m_oLocation.path(oTable.location);
            }
        }


        this.tableLinkClicked = function (oTable) {
            // Check if the sensor link is active
            if (!oTable.isActive){
                // Set the textual description
                this.m_sTableLegendSelected = oTable.description;

                // Reset all actives flag
                this.m_aoTableLinks.forEach(function(oEntry) {
                    oEntry.isActive = false;
                });

                // Set this as the active one
                oTable.isActive = true;


                this.m_oLocation.path(oTable.location);
            }
        }

        this.dataTableLinkClickedByLink = function (sLink) {

            var oDataTable =  null;

            var iTableLinks = 0;

            // Add Additional Axes
            for (iTableLinks=0; iTableLinks<this.m_aoDataTableLinks.length; iTableLinks++) {
                var oTempTableLink = this.m_aoDataTableLinks[iTableLinks];
                if (oTempTableLink != null) {
                    if (oTempTableLink.location == sLink) {
                        oDataTable = oTempTableLink;
                        break;
                    }
                }
            }

            if (oDataTable == null) return;

            // Check if the sensor link is active
            if (!oDataTable.isActive){
                // Set the textual description
                this.m_sDataTableLegendSelected = oDataTable.description;

                // Reset all actives flag
                this.m_aoDataTableLinks.forEach(function(oEntry) {
                    oEntry.isActive = false;
                });

                // Set this as the active one
                oDataTable.isActive = true;

                this.m_sDataTableLegendSelected = oDataTable.description;
            }
        }


        this.dataTableLinkClicked = function (oDataTable) {
            // Check if the sensor link is active
            if (!oDataTable.isActive){
                // Set the textual description
                this.m_sDataTableLegendSelected = oDataTable.description;

                // Reset all actives flag
                this.m_aoDataTableLinks.forEach(function(oEntry) {
                    oEntry.isActive = false;
                });

                // Set this as the active one
                oDataTable.isActive = true;


                this.m_oLocation.path(oDataTable.location);
            }
        }

        this.mapLinkClicked = function() {
            this.m_oLocation.path("/map");
        }


        this.getSummaryTable = function() {
            return this.m_oHttp.get(this.APIURL + '/tables/summary');
        }

        this.getMaxStationsTable = function() {
            return this.m_oHttp.get(this.APIURL + '/tables/max');
        }

    }]);