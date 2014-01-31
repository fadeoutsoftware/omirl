/**
 * Created by p.campanella on 20/01/14.
 */

var TablesController = (function() {
    function TablesController($scope) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
    }

    TablesController.$inject = [
        '$scope'
    ];
    return TablesController;
}) ();
