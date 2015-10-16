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
        //controller  : "ModelsGalleryController",
        replace     : true,
        scope       : {
            "id" : "@id",
            "menuTitle" : "@menuTitle",
            "onMainItemClick" : "=onMainItemClick",
            "onSubItemClick" : "=onSubItemClick",
            "menuItemsList" : "=menuLinkItems",
            "submenuItemsListVarName" : "@submenuListVarName",
            "onSubItemUnclick" : "=onSubItemUnclick"
        },
        link: function($scope, elem, attrs)
        {
            $scope.m_sParentText = ""; //$scope.menuTitle;
            $scope.m_sChildText = ""; //$scope.menuTitle;
            
            if( !$scope.submenuItemsListVarName )
                $scope.submenuItemsListVarName = "submenuItems";
/*
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
            */
            
            
            $scope.submenuItems = [];
            
            $scope.activeMenuItem = null;
            $scope.m_bIsSubmenuVisible = false;
            
            
            $scope.isMenuLinkVisible = function(oMenuLink)
            {
                console.debug("Is visible:", oMenuLink);
                if( oMenuLink )
                {
                    if(oMenuLink.isVisible != null && oMenuLink.isVisible != undefined)
                        return oMenuLink.isVisible;
                    else
                        return true;
                }
                else
                    return false;
            }
            
            
            $scope.isActive = function(menuItem)
            {
               return ($scope.activeMenuItem == menuItem);
            }

            $scope.isSubActive = function(menuItem)
            {
                return ($scope.activeSubItem == menuItem);
            }
            
            
            $scope.mainItemClick = function(item)
            {
                console.debug("mainItemClick:", item);
                
                if( $scope.isActive(item) == true)
                {
                    $scope.m_bIsSubmenuVisible = false;
                    $scope.activeMenuItem = null;
                    
                    $scope.m_sParentText = "";

                    $scope.m_sChildText = "";
                }
                else
                {
                    $scope.activeMenuItem = item;
                    $scope.m_bIsSubmenuVisible = true;
                    
                    $scope.m_sParentText = item.description;

                    $scope.m_sChildText = "";
                    
                    if( item[$scope.submenuItemsListVarName] )
                    {
                        $scope.submenuItems = item[$scope.submenuItemsListVarName];
                    }
                    else
                        item.submenuItems = [];

                    if( typeof $scope.onMainItemClick == "function")
                    {
                        $scope.onMainItemClick(item);
                    }
                }
            }
            
            $scope.submenuItemClick = function(item)
            {

                console.debug("submenuItemClick:", item);

                if( $scope.isSubActive(item) == true)
                {
                    $scope.activeSubItem = null;

                    $scope.m_sParentText = $scope.m_sParentText;

                    $scope.m_sChildText = "";

                    if( typeof $scope.onSubItemUnclick == "function")
                    {
                        $scope.onSubItemUnclick(item);
                    }
                }
                else
                {
                    $scope.activeSubItem = item;

                    $scope.m_sParentText = $scope.m_sParentText;

                    $scope.m_sChildText = item.description;

                    if( typeof $scope.onSubItemClick == "function")
                    {
                        $scope.onSubItemClick(item);
                    }
                }
            }
        }
    }
});

