/**
 * Created by s.adamo on 11/04/2016.
 */

angular.module('omirl.periodService', ['omirl.ConstantsService']).
service('PeriodService', ['$http', 'ConstantsService',  function ($http, oConstantsService) {

    this.m_oHttp = $http;
    this.m_oConstantsService = oConstantsService;
    this.m_aoPeriods = [];
    this.APIURL = oConstantsService.getAPIURL();
    this.m_bModified = false;

    this.setAsModified = function() {
        this.m_bModified = true;
    };

    this.setAsUnchanged = function() {
        this.m_bModified = false;
    };

    this.isModified = function() {
        return this.m_bModified;
    };

    this.loadPeriods  = function() {
        var oPeriodsService = this;
        this.m_oHttp.get(this.APIURL + '/periods/load/').success(function(data){
            if (data == '' || data == null)
                oPeriodsService.m_aoPeriods = [];
            else
                oPeriodsService.m_aoPeriods = data;
        });
    };


    this.getPeriods  = function() {
        return this.m_aoPeriods;
    };

    this.savePeriods = function(oPeriods) {
        this.m_bModified = false;
        return this.m_oHttp.post(this.APIURL + '/periods/save/', oPeriods, {headers: {'Content-Type': 'application/json'}});
    };

    this.deletePeriod = function(id) {
        this.m_bModified = false;
        return this.m_oHttp.post(this.APIURL + '/periods/delete/' + id);
    };

}]);

