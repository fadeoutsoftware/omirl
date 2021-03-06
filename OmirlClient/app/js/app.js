'use strict';

moment.locale("it");

Date.prototype.toString = function() {

    var yyyy = this.getFullYear().toString();
    var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
    var dd  = this.getDate().toString();

    var hh  = this.getHours().toString();
    var min  = this.getMinutes().toString();
    // padding
    return (dd[1]?dd:"0"+dd[0]) + "/" + (mm[1]?mm:"0"+mm[0]) + "/" + yyyy + " " + (hh[1]?hh:"0"+hh[0]) + ":" + (min[1]?min:"0"+min[0]);
}

// Declare app level module which depends on filters, and services
var omirlApp = angular.module('omirl', [
    'ngRoute',
    'ngCookies',
    'az.config',
    'az.directives',
    'az.services',
    'az',
    'ui.bootstrap',
    'ui.bootstrap.datetimepicker',
    'dialogService',
    /*'omirl.stockDirective',*/
    'omirl.chartDirective',
    'omirl.ConstantsService',
    'omirl.authService',
    'omirl.mapNavigatorService',
    'omirl.stationsService',
    'omirl.chartService',
    'omirl.sessionInjector',
    'omirl.TableService',
    'omirl.HydroService',
    'omirl.MapLayerService',
    'omirl.filters',
    'omirl.directives',
    
    'omirl.sidebarMenuDirective',
    'omirl.sidebarMenuLegacyDirective',
    'omirl.galleryService',
    'omirl.animationService',
    'angular-flexslider',
    'pascalprecht.translate',
    'omirl.translateService',
    'omirl.elevateZoomDirective',
    'omirl.userService',
    'omirl.periodService'
]);

angular.module('omirlApp', []).config(function($sceDelegateProvider) {
  $sceDelegateProvider.resourceUrlWhitelist([
    // Allow same origin resource loads.
         'self',
             // Allow loading from our assets domain.  Notice the difference between * and **.
                 'http://omirlclient.cimafoundation.org/**'
                   ]);
    
                     // The blacklist overrides the whitelist so the open redirect here is blocked.
                            });

omirlApp.config(['$httpProvider', '$translateProvider', function($httpProvider, $translateProvider) {
    $httpProvider.interceptors.push('sessionInjector');

    //language configuration
    $translateProvider.useStaticFilesLoader({
        prefix: 'languages/',
        suffix: '.json'
    });


    $translateProvider.preferredLanguage('it');
    $translateProvider.useSanitizeValueStrategy('escaped');

}]);

omirlApp.config(function($routeProvider) {
        $routeProvider.when('/map', {templateUrl: 'partials/map.html', controller: 'MapController'});
        $routeProvider.when('/tables', {templateUrl: 'partials/tables.html', controller: 'TablesController'});
        $routeProvider.when('/animations', {templateUrl: 'partials/animations.html', controller: 'AnimationsController'});
        $routeProvider.when('/animationssat', {templateUrl: 'partials/animationssat.html', controller: 'AnimationsSatController'});
        $routeProvider.when('/credits', {templateUrl: 'partials/credits.html', controller: 'CreditsController'});
        $routeProvider.when('/settings', {templateUrl: 'partials/settings.html', controller: 'SettingsController'});
        $routeProvider.when('/stationstable', {templateUrl: 'partials/stationstable.html', controller: 'StationsTableController'});
        $routeProvider.when('/sensorstable', {templateUrl: 'partials/sensorstable.html', controller: 'SensorTableController'});
        $routeProvider.when('/maxtable', {templateUrl: 'partials/maxtable.html', controller: 'MaxTableController'});
        $routeProvider.when('/summarytable', {templateUrl: 'partials/summarytable.html', controller: 'SummaryTableController'});
        $routeProvider.when('/modelstable', {templateUrl: 'partials/modelstable.html', controller: 'ModelsTableController'});
        $routeProvider.when('/modelsgallery', {templateUrl: 'partials/modelsgallery.html', controller: 'ModelsGalleryController'});
        $routeProvider.when('/users', {templateUrl: 'partials/users.html', controller: 'UsersController'});
        $routeProvider.when('/alertzones', {templateUrl: 'partials/maxhydroalertzones.html', controller: 'MaxHydroAlertZoneController'});
        $routeProvider.when('/periods', {templateUrl: 'partials/periods.html', controller: 'PeriodController'});


        $routeProvider.otherwise({redirectTo: '/map'});
    }
);

omirlApp.controller("MapController", MapController);
omirlApp.controller("TablesController", TablesController);


