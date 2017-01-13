/**
 * Created by p.campanella on 01/02/14.
 */

var TemplateController = (function() {
    function TemplateController($scope, $location, oConstantsService, oAuthService, $log, $route, $templateCache, oTableService, $translate, $interpolate, oTranslateService, $interval, oLayerService, $cookies) {
        this.m_oScope = $scope;
        this.m_oLocation  = $location;
        this.m_oConstantsService = oConstantsService;
        this.m_oAuthService = oAuthService;
        this.m_oTranslationService = oTranslateService;
        this.m_oLayerService = oLayerService;
        this.m_oScope.m_oController = this;
        this.m_bShowLogin = false;
        this.m_bShowLogout = false;
        this.credentials = {};
        this.credentials.userId = "";
        this.credentials.userPassword = "";
        this.ReservedAreaTextConstant = "INDEX_AREARISERVATA";
        this.ReservedAreaText = this.ReservedAreaTextConstant;
        this.m_sLoginMessageConstant = "INDEX_CREDENZIALI";
        this.m_sLoginMessage = this.m_sLoginMessageConstant;
        this.m_bLoginError = false;
        this.m_bLoading = false;
        this.m_oLog = $log;
        this.m_sLastPath = "map";
        this.m_oRoute = $route;
        this.m_oTemplateCache = $templateCache;
        this.m_oTableService = oTableService;
        this.m_oTranslateService = $translate;
        this.m_oInterpolateService = $interpolate;
        this.m_sContainerStyle = "overflow: hidden;";
        this.m_oCookies = $cookies;

        $scope.$on('$locationChangeStart', function(event) {
            resizeMap();
        });

        var sSessionId = this.m_oCookies.omirlId;

        if (sSessionId!=null)
        {
            if (angular.isDefined(sSessionId))
            {
                if (sSessionId != "")
                {
                    var oController = this;
                    this.m_oAuthService.cookieCheck(sSessionId).success(function(data){
                        if (data == null || data == '') {
                            oController.loginresult(null,false);
                        }
                        else {
                            oController.loginresult(data,false);
                        }

                    }).error(function(data, status) {
                        oController.loginresult(data,true);
                    });
                }
            }
        }

        //------------------Session Check - Begin----------------------------------
        var sessionCheck;
        sessionCheck = $interval(function() {
            if ($scope.m_oController.m_oConstantsService.isUserLogged())
                {
                //check session
                $scope.m_oController.m_oAuthService.sessionCheck().success(function(data){
                    if (data == null || data == '') {
                        //session timeout
                        $scope.m_oController.m_oConstantsService.setUser(null);
                        $scope.m_oController.m_bLoading = false;
                        $scope.m_oController.m_oLocation.path("/");
                        $scope.m_oController.credentials.userId = "";
                        $scope.m_oController.credentials.userPassword = "";

                        $scope.m_oController.m_bLoginError = false;

                        $scope.m_oController.ReservedAreaText = this.ReservedAreaTextConstant;
                        $scope.m_oController.m_sLoginMessage = this.m_sLoginMessageConstant;
                        $scope.m_oController.m_bShowLogout = false;
                        $scope.m_oController.m_bShowLogin = true;
                        OmirlMoveTo('#top');
                    }

                });
            }
        }, 120000);
        //------------------Session Check - End----------------------------------

    }


    //************************************************************************************
    //* Accessibility section
    //************************************************************************************
    TemplateController.prototype.isAccessibilityModeActive = function()
    {
        return CookieManager.isAccessibilityModeActive();
    }

    TemplateController.prototype.activateAccessibilityMode = function(){
        vex.dialog.open({
            message: '<div style="text-align: center;"><i class="fa fa-refresh fa-spin fa-3x fa-fw"></i></div>',
            buttons: [],
        });
        CookieManager.setAccessibilityModeActive();
        location.reload(true);
    }
    TemplateController.prototype.deactivateAccessibilityMode = function(){
        vex.dialog.open({
            message: '<div style="text-align: center;"><i class="fa fa-refresh fa-spin fa-3x fa-fw"></i></div>',
            buttons: [],
        });
        CookieManager.setAccessibilityModeNotActive();
        location.reload();
    }
    //************************************************************************************



    TemplateController.prototype.serviceIconClicked = function (sPath) {

        if (sPath == "settings") {
            this.m_sContainerStyle = "overflow: initial;";
        }
        else {
            this.m_sContainerStyle = "overflow: hidden;";
        }

        this.m_sLastPath = sPath;

        this.m_oTableService.tableLinkClickedByLink(sPath);

        this.m_oLocation.path(sPath);
        OmirlMoveTo('#contentcontainer');
    }
    
    TemplateController.prototype.returnToTop = function()
    {
        OmirlMoveTo('#top');
    }


    TemplateController.prototype.reservedAreaClicked = function () {

        if (this.m_oConstantsService.isUserLogged()) {
            this.m_bShowLogin = false;
            this.m_bShowLogout = !this.m_bShowLogout;
        }
        else {
            this.m_bShowLogin = !this.m_bShowLogin;
            this.m_bShowLogout = false;
        }
    }

    TemplateController.prototype.getShowLogout = function() {
        return this.m_bShowLogout;
    }

    TemplateController.prototype.getShowLogin = function() {
        return this.m_bShowLogin;
    }

    TemplateController.prototype.loginresult = function(data, bError) {
        if (!bError) {
            this.credentials.userPassword = "";

            if (!angular.isDefined(data)) data = {};
            this.m_oConstantsService.setUser(data);

            if (this.m_oConstantsService.isUserLogged()) {
                this.credentials.userId = "";
                this.m_oTranslateService('INDEX_BENVENUTO', {nome: this.m_oConstantsService.getUser().name}).then(function(text){
                    this.ReservedAreaText = text;
                });
                this.m_bShowLogin = false;
                this.m_bShowLogout = true;
                this.m_bLoginError = false;

                //oController.serviceIconClicked(oController.m_sLastPath);
                var oCurrentTemplate = this.m_oRoute.current.templateUrl;
                this.m_oTemplateCache.remove(oCurrentTemplate);
                this.m_oScope.$broadcast("$locationChangeStart");
                this.m_oRoute.reload();

                this.m_oCookies.omirlId = data.sessionId;

            }
            else {
                this.m_bLoginError = true;
                this.ReservedAreaText = this.ReservedAreaTextConstant;
                this.m_sLoginMessage = "INDEX_LOGINCRNONCORR";

                this.m_oCookies.omirlId = "";
            }

            this.m_oTableService.refreshTableLinks();
            this.m_bLoading = false;
        }
        else {
            //oController.credentials.userId = "";
            this.credentials.userPassword = "";

            this.m_bLoginError = true;
            this.m_oConstantsService.setUser(null);
            this.ReservedAreaText = this.ReservedAreaTextConstant;
            this.m_sLoginMessage = "INDEX_LOGINCRNONCORR";

            this.m_bLoading = false;

            this.m_oCookies.omirlId = "";
        }
    }

    TemplateController.prototype.login = function() {

        this.m_bLoginError = false;
        this.m_sLoginMessage = "INDEX_LOGIN";

        this.m_bLoading = true;

        var oController = this;

        this.m_oAuthService.login(this.credentials).success(function(data, status) {

            oController.loginresult(data,false);
        }).error(function(data, status) {

            oController.loginresult(data,true);
        });

    }

    TemplateController.prototype.resetLoginMessage = function() {
        this.m_bLoginError = false;
        this.m_sLoginMessage = this.m_sLoginMessageConstant;
    }

    TemplateController.prototype.logout = function() {

        var oController = this;

        this.m_bLoading =true;

        this.m_oAuthService.logout().success(function(data, status) {
            oController.m_oConstantsService.setUser(null);

            oController.m_bLoading =false;

            oController.m_oLocation.path("/");
            OmirlMoveTo('#top');
        }).error(function(data, status) {
            oController.m_bLoading =false;
            oController.m_oConstantsService.setUser(null);
            oController.m_oLog.error('Error contacting Omirl Server');
        });

        this.credentials.userId = "";
        this.credentials.userPassword = "";

        this.m_bLoginError = false;

        this.ReservedAreaText = this.ReservedAreaTextConstant;
        this.m_sLoginMessage = this.m_sLoginMessageConstant;
        this.m_bShowLogout = false;
        this.m_bShowLogin = true;
        this.m_oLayerService.clarAll();
    }

    TemplateController.prototype.changeLanguage = function(language){
        this.m_oTranslateService.use(language);

    }

    TemplateController.$inject = [
        '$scope',
        '$location',
        'ConstantsService',
        'AuthService',
        '$log',
        '$route',
        '$templateCache',
        'TableService',
        '$translate',
        '$interpolate',
        'TranslateService',
        '$interval',
        'az.services.layersService',
        '$cookies'
    ];

    return TemplateController;
}) ();