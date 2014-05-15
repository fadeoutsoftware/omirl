/**
 * Created by p.campanella on 14/05/2014.
 */

var CreditsController = (function() {
    function CreditsController($scope) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
    }

    CreditsController.$inject = [
        '$scope'
    ];
    return CreditsController;
}) ();
