/**
 * Created by s.adamo on 27/10/2015.
 */

'use strict';

angular.module('omirl.userService', ['omirl.ConstantsService']).
    service('UserService', ['$http', 'ConstantsService',  function ($http, oConstantsService) {

        this.m_oHttp = $http;
        this.m_oConstantsService = oConstantsService;
        this.m_aoUsers = [];
        this.APIURL = oConstantsService.getAPIURL();
        this.m_bModified = false;

        this.setAsModified = function() {
            this.m_bModified = true;
        };

        this.isModified = function() {
            return this.m_bModified;
        };

        this.loadUsers  = function() {
            var oUsersService = this;
            this.m_oHttp.get(this.APIURL + '/users/load').success(function(data){
                if (data == '' || data == null)
                    oUsersService.m_aoUsers = [];
                else
                    oUsersService.m_aoUsers = data;
            });
        };


        this.getUsers  = function() {
            return this.m_aoUsers;
        };

        this.saveUsers = function(oUser) {
            this.m_bModified = false;
            return this.m_oHttp.post(this.APIURL + '/users/save/', oUser, {headers: {'Content-Type': 'application/json'}});
        };

        this.deleteUsers = function(id) {
            this.m_bModified = false;
            return this.m_oHttp.post(this.APIURL + '/users/delete/' + id);
        };


    }]);
