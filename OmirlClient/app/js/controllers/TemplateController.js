/**
 * Created by p.campanella on 01/02/14.
 */

var TemplateController = (function() {
    function TemplateController($scope, $location, oConstantsService, oAuthService, $log, $route, $templateCache, oTableService, $translate, $interpolate, oTranslateService, $interval, oLayerService) {
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

        $scope.$on('$locationChangeStart', function(event) {
            resizeMap();
        });

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

    TemplateController.prototype.login = function() {

        this.m_bLoginError = false;
        this.m_sLoginMessage = "INDEX_LOGIN";

        this.m_bLoading = true;

        var oController = this;

        this.m_oAuthService.login(this.credentials).success(function(data, status) {

            oController.credentials.userPassword = "";

            if (!angular.isDefined(data)) data = {};
            oController.m_oConstantsService.setUser(data);

            if (oController.m_oConstantsService.isUserLogged()) {
                oController.credentials.userId = "";
                oController.m_oTranslateService('INDEX_BENVENUTO', {nome: oController.m_oConstantsService.getUser().name}).then(function(text){
                    oController.ReservedAreaText = text;
                });
                oController.m_bShowLogin = false;
                oController.m_bShowLogout = true;
                oController.m_bLoginError = false;

                //oController.serviceIconClicked(oController.m_sLastPath);
                var oCurrentTemplate = oController.m_oRoute.current.templateUrl;
                oController.m_oTemplateCache.remove(oCurrentTemplate);
                oController.m_oScope.$broadcast("$locationChangeStart");
                oController.m_oRoute.reload();
            }
            else {
                oController.m_bLoginError = true;
                oController.ReservedAreaText = oController.ReservedAreaTextConstant;
                oController.m_sLoginMessage = "INDEX_LOGINCRNONCORR";
            }

            oController.m_oTableService.refreshTableLinks();
            oController.m_bLoading = false;
        }).error(function(data, status) {
            //oController.credentials.userId = "";
            oController.credentials.userPassword = "";

            oController.m_bLoginError = true;
            oController.m_oConstantsService.setUser(null);
            oController.ReservedAreaText = oController.ReservedAreaTextConstant;
            oController.m_sLoginMessage = "INDEX_LOGINCRNONCORR";

            oController.m_bLoading = false;
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
        'az.services.layersService'
    ];

    return TemplateController;
}) ();