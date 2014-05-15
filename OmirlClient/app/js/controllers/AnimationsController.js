/**
 * Created by p.campanella on 14/05/2014.
 */

var AnimationsController = (function() {
    function AnimationsController($scope) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
    }

    AnimationsController.$inject = [
        '$scope'
    ];
    return AnimationsController;
}) ();
