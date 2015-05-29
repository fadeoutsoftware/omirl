/**
 * Created by p.campanella on 29/05/2015.
 */

'use strict';
angular.module('omirl.MapLayerService', ['omirl.ConstantsService']).
    service('MapLayerService', ['$http',  'ConstantsService', '$location', function ($http, oConstantsService, oLocation) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;
        this.m_oLocation = oLocation;

        this.getLayerId = function(sCode, sModifier) {
            return this.m_oHttp.get(this.APIURL + '/maps/layer/'+sCode+'/'+sModifier);
        }

    }]);