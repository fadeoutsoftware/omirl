/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

'use strict';
angular.module('omirl.sidebarMenuDirective', [])
    //directive('omirlHighChart', ['ChartService',  function (oChartService) {
.directive("sidebarMenu", function(){
    return {
        restrict    : 'E',
        templateUrl : "partials/SidebarMenuView.html",
        controller  : "",
        replace     : true,
        scope       : {
            "id" : "@id",
            "menuTitle" : "@menuTitle",
            "onMainItemClick" : "=onMainItemClick",
        },
        link: function($scope, elem, attrs)
        {
            $scope.m_sLegendText = ""; //$scope.menuTitle;
            
            $scope.menuItemsList = [
                {
                    iconSrc : "img/wet.png",
                    description : "Descrizione di test 1"
                },
                {
                    iconSrc : "img/rain_drops.png",
                    description : "Descrizione di test 2",
                    submenuItems : [
                        {
                            iconSrc : "img/15m.png",
                            description : "15 minuti"
                        },
                        {
                            iconSrc : "img/30m.png",
                            description : "30 minuti"
                        }
                    ]
                }
            ];
            
            $scope.submenuItems = [];
            
            $scope.activeMenuItem = null;
            $scope.m_bIsSubmenuVisible = false;
            
            
            $scope.isActive = function(menuItem)
            {
               return ($scope.activeMenuItem == menuItem);
            }
            
            
            $scope.mainItemClick = function(item)
            {
                console.debug("mainItemClick:", item);
                
                if( $scope.isActive(item) == true)
                {
                    $scope.m_bIsSubmenuVisible = false;
                    $scope.activeMenuItem = null;
                    
                    $scope.m_sLegendText = "";
                }
                else
                {
                    $scope.activeMenuItem = item;
                    $scope.m_bIsSubmenuVisible = true;
                    
                    $scope.m_sLegendText = item.description;
                    
                    if( item.submenuItems )
                        $scope.submenuItems = item.submenuItems;
                    else
                        item.submenuItems = [];

                    if( typeof $scope.onMainItemClick == "function")
                    {
                        $scope.onMainItemClick();
                    }
                }
            }
            
            $scope.submenuItemClick = function(item)
            {
                console.debug("submenuItemClick:", item);
            }
        }
    }
});