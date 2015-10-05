/**
 * Created by p.campanella on 20/01/14.
 */

var TablesController = (function() {
    function TablesController($scope, $location) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oLocation = $location;

        this.m_sTableLegendSelected = "";


    }


    TablesController.prototype.linkClicked = function (sPath) {
        this.m_oLocation.path(sPath);
    }


    TablesController.$inject = [
        '$scope',
        '$location',
        'ConstantsService'

    ];

    return TablesController;
}) ();
