/**
 * Created by p.campanella on 14/05/2014.
 */

var AnimationsSatController = (function() {
    function AnimationsSatController($scope, $translate, $location, oConstantService, $interval, oAnimationService) {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oTranslateService = $translate;
        this.m_oLocation = $location;
        this.m_oConstantsService = oConstantService;
        this.m_oInterval = $interval;
        this.m_oAnimationService = oAnimationService;
        this.m_sActiveCode = "";
        this.m_sActiveLink = "";


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

    AnimationsSatController.prototype.isActive = function (sCode) {
        return sCode == this.m_sActiveCode;
    }

    AnimationsSatController.prototype.setImage = function (sCode) {
        var oController = this;

        this.m_sActiveCode = sCode + 'Img';

        this.m_oAnimationService.getImage(sCode).success(function(data, status, headers, config){
            if (angular.isDefined(data))
            {
                oController.m_sActiveLink = data.StringValue;

                if (oController.m_sActiveLink == null)
                {
                    oController.m_sActiveLink = 'img/nodata.jpg'
                }
            }
        }).error(function(data, status, headers, config) {
            console.error("Fail to do GET:");
        });
    }

    AnimationsSatController.prototype.setAnimation = function (sCode) {
        var oController = this;

        this.m_sActiveCode = sCode + 'Ani';

        this.m_oAnimationService.getAnimation(sCode).success(function(data, status, headers, config){
            if (angular.isDefined(data))
            {
                oController.m_sActiveLink = data.StringValue;

                if (oController.m_sActiveLink == null)
                {
                    oController.m_sActiveLink = 'img/nodata.jpg'
                }
            }
        }).error(function(data, status, headers, config) {
            console.error("Fail to do GET:");
        });
    }


    AnimationsSatController.$inject = [
        '$scope',
        '$translate',
        '$location',
        'ConstantsService',
        '$interval',
        'AnimationsService'
    ];

    return AnimationsSatController;
}) ();
