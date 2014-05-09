'use strict';


// Declare app level module which depends on filters, and services
var omirlApp = angular.module('omirl', [
    'ngRoute',
    'az.config',
    'az.directives',
    'az.services',
    'az',
    'ui.bootstrap',
    'omirl.ConstantsService',
    'omirl.mapNavigatorService',
    'omirl.stationsService',
    'omirl.filters',
    'omirl.directives'
]);


omirlApp.config(function($routeProvider) {
        $routeProvider.when('/map', {templateUrl: 'partials/map.html', controller: 'MapController'});
        $routeProvider.when('/tables', {templateUrl: 'partials/tables.html', controller: 'TablesController'});
        $routeProvider.otherwise({redirectTo: '/map'});
    }
);

omirlApp.controller("MapController", MapController);
omirlApp.controller("TablesController", TablesController);


