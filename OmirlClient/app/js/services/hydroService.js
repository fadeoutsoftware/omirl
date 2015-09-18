/**
 * Created by p.campanella on 12/11/2014.
 */

'use strict';
angular.module('omirl.HydroService', ['omirl.ConstantsService']).
    service('HydroService', ['$http',  'ConstantsService', '$location', function ($http, oConstantsService, oLocation) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;
        this.m_oLocation = oLocation;
        this.m_bLinksFetched = false;

        this.m_aoModelLinks = [
             {
                 "code": "Ensemble",
                 "description": "Ensemble Multimodel",
                 "imageLinkOff": "img/hydro/hydroDet.png",
                 "isActive": false
             },
             {
                 "code": "Soggettiva",
                 "description": "Soggettiva",
                 "imageLinkOff": "img/hydro/hydroSoggettiva.png",
                 "isActive": false
             },
             {
                 "code": "RainfarmBo00",
                 "description": "Rainfarm Bolam 10ar +00",
                 "imageLinkOff": "img/hydro/hydroBolam00.png",
                 "isActive": false
             },
             {
                 "code": "RainfarmBo06",
                 "description": "Rainfarm Bolam 10ar +06",
                 "imageLinkOff": "img/hydro/hydroBolam06.png",
                 "isActive": false
             },
            {
                "code": "RainfarmBo-06",
                "description": "Rainfarm Bolam 10ar -06",
                "imageLinkOff": "img/hydro/hydroBolam-06.png",
                "isActive": false
            },
            {
                "code": "RainfarmBo-12",
                "description": "Rainfarm Bolam 10ar -12",
                "imageLinkOff": "img/hydro/hydroBolam12.png",
                "isActive": false
            },
            {
                "code": "RainfarmMo00",
                "description": "Rainfarm Moloch 02ar +00",
                "imageLinkOff": "img/hydro/hydroMoloch00.png",
                "isActive": false
            },
            {
                "code": "RainfarmMo06",
                "description": "Rainfarm Moloch 02ar +06",
                "imageLinkOff": "img/hydro/hydroMoloch06.png",
                "isActive": false
            },
            {
                "code": "RainfarmMo-06",
                "description": "Rainfarm Moloch 02ar -06",
                "imageLinkOff": "img/hydro/hydroMoloch-06.png",
                "isActive": false
            },
            {
                "code": "RainfarmMo-12",
                "description": "Rainfarm Moloch 02ar -12",
                "imageLinkOff": "img/hydro/hydroMoloch12.png",
                "isActive": false
            },
            {
                "code": "RainfarmLm07",
                "description": "Rainfarm Lami 07cinar ",
                "imageLinkOff": "img/hydro/hydroMoloch00.png",
                "isActive": false
            },
            {
                "code": "RainfarmLm-12",
                "description": "Rainfarm Lami 07cinar -12",
                "imageLinkOff": "img/hydro/hydroMoloch12.png",
                "isActive": false
            },
        ];

        this.m_aoModelTable = [
            {
                "Basin" : "Nervia",
                "BasinColor" : "",
                "Section1" : "Nervia foce",
                "Section1Code" : "",
                "Section1BgColor" : "models-table-green-cell",
                "Section2" : "Isolabona",
                "Section2Code" : "",
                "Section2BgColor" : "models-table-green-cell",
                "Section3" : "Buggio",
                "Section3Code" : "models-table-red-cell",
                "Section3BgColor" : "",
                "Section4" : "",
                "Section4Code" : "",
                "Section4BgColor" : "",
                "Section5" : "",
                "Section5Code" : "",
                "Section5BgColor" : "",
                "Section6" : "",
                "Section6Code" : "",
                "Section6BgColor" : ""
            },
            {
                "Basin" : "tra Nervia e Argentina",
                "BasinColor" : "",
                "Section1" : "Vallecrosia foce",
                "Section1Code" : "",
                "Section1BgColor" : "models-table-green-cell",
                "Section2" : "Arnea foce",
                "Section2Code" : "",
                "Section2BgColor" : "models-table-red-cell",
                "Section3" : "Armea Valle Armea",
                "Section3Code" : "",
                "Section3BgColor" : "models-table-green-cell",
                "Section4" : "",
                "Section4Code" : "",
                "Section4BgColor" : "",
                "Section5" : "",
                "Section5Code" : "",
                "Section5BgColor" : "",
                "Section6" : "",
                "Section6Code" : "",
                "Section6BgColor" : ""
            },
            {
                "Basin" : "Argentina",
                "BasinColor" : "",
                "Section1" : "Argentina foce",
                "Section1Code" : "",
                "Section1BgColor" : "models-table-green-cell",
                "Section2" : "Merelli",
                "Section2Code" : "",
                "Section2BgColor" : "models-table-green-cell",
                "Section3" : "Montalto Ligure",
                "Section3Code" : "",
                "Section3BgColor" : "models-table-green-cell",
                "Section4" : "",
                "Section4Code" : "",
                "Section4BgColor" : "",
                "Section5" : "",
                "Section5Code" : "",
                "Section5BgColor" : "",
                "Section6" : "",
                "Section6Code" : "",
                "Section6BgColor" : ""
            }
        ];

        this.getModelLinks = function() {
            //return this.m_aoModelLinks;
            return this.m_oHttp.get(this.APIURL + '/tables/hydromodellist');

        }

        this.getModelTable = function(sModelCode) {
            //return this.m_aoModelTable;
            return this.m_oHttp.get(this.APIURL + '/tables/sectionbasinlist/' + sModelCode);
        }


        this.getSections = function(oSectionLink) {
            return this.m_oHttp.get(this.APIURL + '/sections/'+oSectionLink.linkCode);
        }
    }]);