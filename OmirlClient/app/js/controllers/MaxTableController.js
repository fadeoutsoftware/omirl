/**
 * Created by p.campanella on 24/09/2014.
 */

var MaxTableController = (function() {
    function MaxTableController($scope, $log, $location, oConstantService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLog = $log;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
    }

    MaxTableController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }


    MaxTableController.$inject = [
        '$scope',
        '$log',
        '$location',
        'ConstantsService'
    ];
    return MaxTableController;
}) ();
