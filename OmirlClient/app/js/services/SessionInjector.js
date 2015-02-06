/**
 * Created by p.campanella on 22/08/2014.
 */

angular.module('omirl.sessionInjector', ['omirl.ConstantsService']).
    factory('sessionInjector', ['ConstantsService', function(oConstantsService) {
    var sessionInjector = {
        request: function(config) {

            var oDate = '';
            if (oConstantsService.getReferenceDate() != '') {
                var oYear = oConstantsService.getReferenceDate().getFullYear();
                var oMonth = oConstantsService.getReferenceDate().getMonth() + 1;
                var oDay = oConstantsService.getReferenceDate().getDate();
                var oHour = oConstantsService.getReferenceDate().getHours();
                var oMin = oConstantsService.getReferenceDate().getMinutes();
                oDate = oYear + '/' + oMonth + '/' + oDay + ' ' + oHour + ':' + oMin;
                //oDate = oConstantsService.getReferenceDate();
            }

            config.headers['x-session-token'] = oConstantsService.getSessionId();
            config.headers['x-refdate'] = oDate;
            return config;
        }
    };
    return sessionInjector;
}]);