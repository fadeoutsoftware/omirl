/**
 * Created by p.campanella on 14/05/2014.
 */

var AnimationsController = (function() {
    function AnimationsController($scope, $translate, $location, oConstantService, $interval, oAnimationService) {
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
        this.m_bMenuRadActive = true;
        this.m_bMenuSatActive = false;
        //-----------------------------------------------------------

        // Flag to know if the side bar is collapsed or not
        this.m_bSideBarCollapsed = false;


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

    AnimationsController.prototype.linkClicked = function (sPath) {

        this.m_oLocation.path(sPath);

    }

    AnimationsController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.m_bNowMode = false;
    };

    AnimationsController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
    };

    AnimationsController.prototype.isActive = function (sCode) {
        return sCode == this.m_sActiveCode;
    }

    AnimationsController.prototype.setImage = function (sCode) {
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

    AnimationsController.prototype.setAnimation = function (sCode) {
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

    AnimationsController.prototype.toggleSideBarClicked = function() {

        var oElement = angular.element("#mapNavigation");

        if (oElement != null) {
            if (oElement.length>0) {
                var iWidth = oElement[0].clientWidth;
                iWidth -= 0;

                if (!this.m_bSideBarCollapsed) {
                    oElement[0].style.left = "-" + iWidth + "px";
                }
                else {
                    oElement[0].style.left =  "0px";
                }

                //oElement.sty
            }
        }

        this.m_bSideBarCollapsed = !this.m_bSideBarCollapsed;
    }

    AnimationsController.$inject = [
        '$scope',
        '$translate',
        '$location',
        'ConstantsService',
        '$interval',
        'AnimationsService'
    ];
    return AnimationsController;
}) ();
