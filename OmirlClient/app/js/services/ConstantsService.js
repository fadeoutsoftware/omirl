/**
 * Created by p.campanella on 27/01/14.
 */

'use strict';
angular.module('omirl.ConstantsService', []).
    service('ConstantsService', ['$http',  function ($http) {
        this.APIURL = 'http://localhost:8080/it.fadeout.mercurius.webapi/rest';

        this.getAPIURL = function() {
            return this.APIURL;
        }
    }]);

