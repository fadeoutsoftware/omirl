/**
 * Created by p.campanella on 27/01/14.
 */

'use strict';
angular.module('omirl.mapNavigatorService', []).
    service('MapNavigatorService', ['$http',  function ($http) {
        this.APIURL = 'http://localhost:8080/it.fadeout.mercurius.webapi/rest';

        this.m_oHttp = $http;

        this.getMapFirstLevels = function() {
            var aoMapLinks = [
                {
                    "link":"img/rain_drops.png",
                    "linkId":1,
                    "selected": false,
                    "hasThirdLevel": false,
                    "description":"Pioggia",
                    "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                    "layerID": ""
                },
                {
                    "link":"img/wet.png",
                    "linkId":2,
                    "selected": false,
                    "hasThirdLevel": false,
                    "description":"Bagnamento",
                    "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                    "layerID": ""
                },
                {
                    "link":"img/thermometer.png",
                    "linkId":3,
                    "selected": false,
                    "hasThirdLevel": false,
                    "description":"Temperatura",
                    "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                    "layerID": ""
                }
            ];

            return aoMapLinks;
        }

        this.getMapSecondLevels = function(linkId) {
            if(linkId==1) {
                var aoMapLinks = [
                    {
                        "link":"img/15m.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultimi 15'",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall15m"
                    },
                    {
                        "link":"img/30m.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultimi 30'",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall30m"
                    },
                    {
                        "link":"img/1h.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultima Ora",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall1h"
                    },
                    {
                        "link":"img/3h.png",
                        "linkId":2,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultime 3 Ore",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall3h"
                    },
                    {
                        "link":"img/6h.png",
                        "linkId":3,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultime 6 Ore",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall3h"
                    },
                    {
                        "link":"img/12h.png",
                        "linkId":4,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultime 12 Ore",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall3h"
                    },
                    {
                        "link":"img/24h.png",
                        "linkId":5,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultime 24 Ore",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall24h"
                    },
                    {
                        "link":"img/7d.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultimi 7 Giorni",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall24h"
                    },
                    {
                        "link":"img/15d.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultimi 15 Giorni",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall15d"
                    },
                    {
                        "link":"img/30d.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Ultimi 30 Giorni",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:rainfall30d"
                    }
                ];

                return aoMapLinks;
            }
            else if (linkId == 2) {
                var aoMapLinks = [
                    {
                        "link":"img/wet0.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Stato alle 00:00",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:humidity00"
                    },
                    {
                        "link":"img/wet6.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Stato alle 06:00",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:humidity06"
                    },
                    {
                        "link":"img/wet12.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Stato alle 12:00",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:humidity12"
                    },
                    {
                        "link":"img/wet18.png",
                        "linkId":2,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Stato alle 18:00",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:humidity12"
                    },
                    {
                        "link":"img/wetstart.png",
                        "linkId":3,
                        "selected": false,
                        "hasThirdLevel": true,
                        "description":"Condizioni Iniziali",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "OMIRL:humidityStart"
                    }
                ];

                return aoMapLinks;
            }
            else if (linkId == 3) {
                var aoMapLinks = [
                    {
                        "link":"img/temp-min.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": false,
                        "description":"Minima",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "tempMin"
                    },
                    {
                        "link":"img/temp-avg.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": false,
                        "description":"Media",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "tempMean"
                    },
                    {
                        "link":"img/temp-max.png",
                        "linkId":1,
                        "selected": false,
                        "hasThirdLevel": false,
                        "description":"Massima",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "tempMax"
                    },
                    {
                        "link":"img/theta.png",
                        "linkId":2,
                        "selected": false,
                        "hasThirdLevel": false,
                        "description":"Theta da Media",
                        "layerWMS": "http://www.nfsproject.com/geoserver/OMIRL/wms",
                        "layerID": "tempTheta"
                    }
                ];

                return aoMapLinks;
            }
        }

        this.getMapThirdLevel = function(oMapItem) {
            var aoMapLinks = [
                {
                    "isDefault": true,
                    "mapItem": oMapItem,
                    "description":"Interpolata",
                    "layerIDModifier": ""
                },
                {
                    "isDefault": false,
                    "mapItem": oMapItem,
                    "description":"Comuni",
                    "layerIDModifier": "Com"
                },
                {
                    "isDefault": false,
                    "mapItem": oMapItem,
                    "description":"Bacini",
                    "layerIDModifier": "Bac"
                },
                {
                    "isDefault": false,
                    "mapItem": oMapItem,
                    "description":"Aree Allertamento",
                    "layerIDModifier": "AA"
                }
            ];

            return aoMapLinks;
        }


        this.getSensorFirstLevel = function() {
            var aoSensorFirstLevels = [
                {
                    "code": "Pluvio",
                    "description": "Precipitazione",
                    "imageLinkOn": "img/sensors/pluviometriOn.png",
                    "imageLinkOff": "img/sensors/pluviometriOff.png",
                    "count": 50,
                    "isActive": false
                },
                {
                    "code": "Termo",
                    "description": "Termometri",
                    "imageLinkOn": "img/sensors/temperaturaOn.png",
                    "imageLinkOff": "img/sensors/temperaturaOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Idro",
                    "description": "Idrometri",
                    "imageLinkOn": "img/sensors/idrometriOn.png",
                    "imageLinkOff": "img/sensors/idrometriOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Vento",
                    "description": "Vento",
                    "imageLinkOn": "img/sensors/ventoOn.png",
                    "imageLinkOff": "img/sensors/ventoOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Igro",
                    "description": "Umidita' del Suolo",
                    "imageLinkOn": "img/sensors/igrometriOn.png",
                    "imageLinkOff": "img/sensors/igrometriOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Radio",
                    "description": "Radiazione Solare",
                    "imageLinkOn": "img/sensors/radiazioneOn.png",
                    "imageLinkOff": "img/sensors/radiazioneOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Foglie",
                    "description": "Bagnatura Foliare",
                    "imageLinkOn": "img/sensors/fogliareOn.png",
                    "imageLinkOff": "img/sensors/fogliareOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Press",
                    "description": "Pressione Atmosferica",
                    "imageLinkOn": "img/sensors/pressioneOn.png",
                    "imageLinkOff": "img/sensors/pressioneOff.png",
                    "count": 43,
                    "isActive": false
                },
                {
                    "code": "Batt",
                    "description": "Tensione Batteria",
                    "imageLinkOn": "img/sensors/batteriaOn.png",
                    "imageLinkOff": "img/sensors/batteriaOff.png",
                    "count": 43,
                    "isActive": false
                }
            ];

            return aoSensorFirstLevels;
        }
    }]);

