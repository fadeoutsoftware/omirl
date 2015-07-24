/**
 * Created by p.campanella on 01/02/14.
 */

var MiniController = (function() {
    function MiniController($scope, $location, oConstantsService, oAuthService, $log, $route, $templateCache, oTableService, $translate) {
        this.m_oScope = $scope;
        this.m_oLocation  = $location;
        this.m_oConstantsService = oConstantsService;
        this.m_oAuthService = oAuthService;
        this.m_oScope.m_oController = this;
        this.m_bShowLogin = false;
        this.m_bShowLogout = false;
        this.credentials = {};
        this.credentials.userId = "";
        this.credentials.userPassword = "";
        this.ReservedAreaTextConstant = "Area Riservata";
        this.ReservedAreaText = this.ReservedAreaTextConstant;
        this.m_sLoginMessageConstant = "Inserire le credenziali";
        this.m_sLoginMessage = this.m_sLoginMessageConstant;
        this.m_bLoginError = false;
        this.m_bLoading = false;
        this.m_oLog = $log;
        this.m_sLastPath = "map";
        this.m_oRoute = $route;
        this.m_oTemplateCache = $templateCache;
        this.m_oTableService = oTableService;
        this.m_oTranslateService = $translate;
        this.m_sContainerStyle = "overflow: hidden;";

        oConstantsService.setIsMiniVersion(true);

        $scope.$on('$locationChangeStart', function(event) {
            resizeMap();
        });
    }

    MiniController.prototype.serviceIconClicked = function (sPath) {

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


    MiniController.prototype.reservedAreaClicked = function () {

        if (this.m_oConstantsService.isUserLogged()) {
            this.m_bShowLogin = false;
            this.m_bShowLogout = !this.m_bShowLogout;
        }
        else {
            this.m_bShowLogin = !this.m_bShowLogin;
            this.m_bShowLogout = false;
        }
    }

    MiniController.prototype.getShowLogout = function() {
        return this.m_bShowLogout;
    }

    MiniController.prototype.getShowLogin = function() {
        return this.m_bShowLogin;
    }

    MiniController.prototype.login = function() {

        this.m_bLoginError = false;
        this.m_sLoginMessage = "Accesso in corso...";

        this.m_bLoading = true;

        var oController = this;

        this.m_oAuthService.login(this.credentials).success(function(data, status) {

            oController.credentials.userPassword = "";

            if (!angular.isDefined(data)) data = {};
            oController.m_oConstantsService.setUser(data);

            if (oController.m_oConstantsService.isUserLogged()) {
                oController.credentials.userId = "";
                oController.ReservedAreaText = "Benvenuto " + oController.m_oConstantsService.getUser().name;
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
                oController.m_sLoginMessage = "Credenziali non corrette";
            }

            oController.m_oTableService.refreshTableLinks();
            oController.m_bLoading = false;
        }).error(function(data, status) {
            //oController.credentials.userId = "";
            oController.credentials.userPassword = "";

            oController.m_bLoginError = true;
            oController.m_oConstantsService.setUser(null);
            oController.ReservedAreaText = oController.ReservedAreaTextConstant;
            oController.m_sLoginMessage = "Credenziali non corrette";

            oController.m_bLoading = false;
        });

    }

    MiniController.prototype.resetLoginMessage = function() {
        this.m_bLoginError = false;
        this.m_sLoginMessage = this.m_sLoginMessageConstant;
    }

    MiniController.prototype.logout = function() {

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
    }

    MiniController.$inject = [
        '$scope',
        '$location',
        'ConstantsService',
        'AuthService',
        '$log',
        '$route',
        '$templateCache',
        'TableService',
        '$translate'
    ];

    return MiniController;
}) ();