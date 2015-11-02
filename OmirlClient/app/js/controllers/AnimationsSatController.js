/**
 * Created by p.campanella on 14/05/2014.
 */

var AnimationsSatController = (function() {
    function AnimationsSatController($scope, $translate, $location, oConstantService, $interval) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oTranslateService = $translate;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
        this.m_oInterval = $interval;

        //---Per ora il menù lo gestiamo così visto che è un mockup---
        this.m_bMenuRadActive = false;
        this.m_bMenuSatActive = true;
        //-----------------------------------------------------------

        var oControllerVar = this;

        if (this.m_oConstantsService.isNowMode()) {
            oControllerVar.m_oReferenceDate = new Date();
            oControllerVar.m_bNowMode = true;
        }
        else {
            oControllerVar.m_oReferenceDate = oControllerVar.m_oConstantsService.getReferenceDate();
            oControllerVar.m_bNowMode = false;
        }

        // Add Auto Refresh Interval Callback
        this.m_oStopTimerPromise = this.m_oInterval(function () {

                if (oControllerVar.m_oConstantsService.isNowMode()) {
                    oControllerVar.m_oReferenceDate = new Date();
                    oControllerVar.m_bNowMode = true;
                }

            },
            this.m_oConstantsService.getRefreshRateMs());

    }

    AnimationsSatController.prototype.linkClicked = function (sPath) {

        this.m_oLocation.path(sPath);

    }

    AnimationsSatController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.m_bNowMode = false;
    };

    AnimationsSatController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
    };

    AnimationsSatController.$inject = [
        '$scope',
        '$translate',
        '$location',
        'ConstantsService',
        '$interval'
    ];

    return AnimationsSatController;
}) ();
