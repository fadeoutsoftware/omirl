'use strict';


// Declare app level module which depends on filters, and services
var omirlApp = angular.module('omirl', [
    'ngRoute',
    'az.config',
    'az.directives',
    'az.services',
    'az',
    'ui.bootstrap',
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
    'omirl.filters',
    'omirl.directives'
]);

omirlApp.config(['$httpProvider', function($httpProvider) {
    $httpProvider.interceptors.push('sessionInjector');
}]);

omirlApp.config(function($routeProvider) {
        $routeProvider.when('/map', {templateUrl: 'partials/map.html', controller: 'MapController'});
        $routeProvider.when('/tables', {templateUrl: 'partials/tables.html', controller: 'TablesController'});
        $routeProvider.when('/animations', {templateUrl: 'partials/animations.html', controller: 'AnimationsController'});
        $routeProvider.when('/credits', {templateUrl: 'partials/credits.html', controller: 'CreditsController'});
        $routeProvider.when('/settings', {templateUrl: 'partials/settings.html', controller: 'SettingsController'});
        $routeProvider.when('/stationstable', {templateUrl: 'partials/stationstable.html', controller: 'StationsTableController'});
        $routeProvider.when('/sensorstable', {templateUrl: 'partials/sensorstable.html', controller: 'SensorTableController'});
        $routeProvider.when('/maxtable', {templateUrl: 'partials/maxtable.html', controller: 'MaxTableController'});
        $routeProvider.when('/summarytable', {templateUrl: 'partials/summarytable.html', controller: 'SummaryTableController'});
        $routeProvider.when('/modelstable', {templateUrl: 'partials/modelstable.html', controller: 'ModelsTableController'});

        $routeProvider.otherwise({redirectTo: '/map'});
    }
);

omirlApp.controller("MapController", MapController);
omirlApp.controller("TablesController", TablesController);


