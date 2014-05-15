/**
 * Created by p.campanella on 01/02/14.
 */

var TemplateController = (function() {
    function TemplateController($scope, $location) {
        this.m_oScope = $scope;
        this.m_oLocation  = $location;
        this.m_oScope.m_oController = this;
    }

    TemplateController.prototype.serviceIconClicked = function (sPath) {
        // TODO: così non va lo scroll...
        this.m_oLocation.path(sPath);
        OmirlMoveTo('#contentcontainer');
    }


    TemplateController.$inject = [
        '$scope',
        '$location'
    ];

    return TemplateController;
}) ();