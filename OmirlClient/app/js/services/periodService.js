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

    this.ConvertToLocalTime = function(date) {
        var utcDate = moment.utc(date).format('YYYY-MM-DDTHH:mm:ss')
        var localTime  = moment.utc(utcDate).toDate();
        localTime = moment(localTime).format('DD/MM/YYYY HH:mm');
        return localTime;
    }

    this.loadPeriods  = function() {
        var oPeriodsService = this;
        this.m_oHttp.get(this.APIURL + '/periods/load/').success(function (data) {
            oPeriodsService.m_aoPeriods = [];
            for (var iCount = 0; iCount < data.length; iCount++) {

                var dtStart = moment(data[iCount].timestampStart + 'Z', 'DD/MM/YYYY HH:mm:ssZ');
                var dtEnd = moment(data[iCount].timestampEnd + 'Z', 'DD/MM/YYYY HH:mm:ssZ');
                var utcStart = moment.utc(dtStart).toDate();
                var utcEnd = moment.utc(dtEnd).toDate();

                data[iCount].timestampStart = moment(utcStart).format('DD/MM/YYYY HH:mm');
                data[iCount].timestampEnd = moment(utcEnd).format('DD/MM/YYYY HH:mm');
                oPeriodsService.m_aoPeriods.push(data[iCount]);
            }


        });
    };


    this.getPeriods  = function() {
        return this.m_aoPeriods;
    };

    this.savePeriods = function(oPeriods) {
        this.m_bModified = false;
        //convert local time to utc
        if (!angular.isUndefined(oPeriods) && oPeriods != null)
        {


            oPeriods.timestampStart = moment(oPeriods.timestampStart, 'DD/MM/YYYY HH:mm').utc().format('DD/MM/YYYY HH:mm');
            oPeriods.timestampEnd = moment(oPeriods.timestampEnd, 'DD/MM/YYYY HH:mm').utc().format('DD/MM/YYYY HH:mm');
        }
        return this.m_oHttp.post(this.APIURL + '/periods/save/', oPeriods, {headers: {'Content-Type': 'application/json'}});
    };

    this.deletePeriod = function(id) {
        this.m_bModified = false;
        return this.m_oHttp.post(this.APIURL + '/periods/delete/' + id);
    };

}]);

