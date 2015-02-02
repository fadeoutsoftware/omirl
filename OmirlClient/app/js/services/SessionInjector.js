/**
 * Created by p.campanella on 22/08/2014.
 */

angular.module('omirl.sessionInjector', ['omirl.ConstantsService']).
    factory('sessionInjector', ['ConstantsService', function(oConstantsService) {
    var sessionInjector = {
        request: function(config) {
            config.headers['x-session-token'] = oConstantsService.getSessionId();
            config.headers['x-refdate'] = oConstantsService.getReferenceDate();
            return config;
        }
    };
    return sessionInjector;
}]);