/**
 * Created by p.campanella on 24/09/2014.
 */

var IdroModelTableController = (function() {
    function IdroModelTableController($scope, $log, $location, oConstantService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
    }

    IdroModelTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }


    IdroModelTableController.$inject = [
        '$scope',
        '$log',
        '$location',
        'ConstantsService'
    ];
    return IdroModelTableController;
}) ();
