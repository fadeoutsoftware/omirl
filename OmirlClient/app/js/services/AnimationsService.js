/**
 * Created by Paolo Campanella on 30/12/2015
 */

'use strict';
angular.module('omirl.animationService', ['omirl.ConstantsService']).
    service('AnimationsService', ['$http',  'ConstantsService', function ($http, oConstantsService) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;
        this.m_oGalleryLinks = null;
        var oScope = this;

        this.getAnimation = function(code) {
            return this.m_oHttp.get(this.APIURL + '/animation/animation/' + code);
        }

        this.getImage = function(code) {
            return this.m_oHttp.get(this.APIURL + '/animation/image/' + code);
        }

    }]);

