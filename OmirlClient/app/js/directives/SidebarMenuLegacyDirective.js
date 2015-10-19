/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

'use strict';

//**************************************************************
//* Legacy version to maintain compatibility with old menu
//**************************************************************
angular.module('omirl.sidebarMenuLegacyDirective', [])
    //directive('omirlHighChart', ['ChartService',  function (oChartService) {
.directive("sidebarMenuLegacy", function(){
    return {
        restrict    : 'E',
        templateUrl : "partials/SidebarMenuLegacyView.html",
        //controller  : "ModelsGalleryController",
        replace     : true,
        scope       : {
            "id" : "@id",
            "onInit" : "=onInit",
            "controller" : "=controller",
            "menuLegendSelected" : "=menuLegendSelected",
            "menuLegendHover" : "=menuLegendHover",
            "isFirstLevel" : "=isFirstLevel",
            "onMenutItemClick" : "=onMenuItemClick",
            "firstLevelMenuItemsList" : "=firstLevelMenuItems",
            "secondLevelMenuItemsList" : "=secondLevelMenuItems",
            //"getMenuItems" : "=getMenuItemsMethod",
            "thirdLevelMenuItems" : "=thirdLevelMenuItems",
            "thirdLevelMenuSelection" : "=thirdLevelMenuSelectedItem",
            "thirdLevelMenuClick" : "=thirdLevelMenuClick"
        },
        link: function($scope, elem, attrs)
        {
            $scope.MENU_LEVEL_1 = 0;
            $scope.MENU_LEVEL_2 = 1;
            $scope.MENU_LEVEL_3 = 2;
            
            
            $scope.m_sParentText = ""; //$scope.menuTitle;
            $scope.m_sChildText = ""; //$scope.menuTitle;
            
            
            $scope.menuItemsByLevel = [];
            $scope.selectedMenuItemByLevel = [];
            $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_1] = null;
            $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_2] = null;
            $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_3] = null;
            
            
            $scope.thirdLevelComboBox = {
                selection: null,
                options: [],
               };
            
            
            if( !$scope.submenuItemsListVarName )
                $scope.submenuItemsListVarName = "submenuItems";
            
            $scope.firstLevelMenuRows = [];
            $scope.activeMenuItem = null;
            
            $scope.submenuItems = [];
            $scope.m_bIsSubmenuVisible = false;
            $scope.secondLevelMenuOpened = -1;
            
            
            
            $scope.hasMenuItemBeenUpdated = true;
            
            
            $scope.menuLevelToUpdate = $scope.MENU_LEVEL_1;
            
            $scope.isThirdLevelVisible = false;
            
            
            //****************************************************************************************
            //* Directive listeners
            //****************************************************************************************
            $scope.$watch("firstLevelMenuItemsList", function(newValue){               
               $scope.updateMapLinks();
               $scope.hasMenuItemBeenUpdated = true;
            });
            $scope.$watch("secondLevelMenuItemsList", function(newValue){               
               $scope.updateMapLinks();
               $scope.hasMenuItemBeenUpdated = true;
            });
            
            
            $scope.$watch("thirdLevelMenuItems", function(newValue){               
               $scope.updateMapLinks();
               $scope.hasMenuItemBeenUpdated = true;
            });
            
            $scope.$watch(function(){ return $scope.thirdLevelComboBox.selection;}, function(newValue){
                $scope.onThirdLevelSelection(newValue);
            });
            
            $scope.$watch("thirdLevelMenuSelection", function(newValue){
               $scope.thirdLevelComboBox.selection = $scope.thirdLevelMenuSelection;
            });
            
            
            
            
            
            
            //****************************************************************************************
            //* Directive scope methods
            //****************************************************************************************
            
            $scope.isMenuLinkVisible = function(oMenuLink)
            {
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
            
            $scope.closeMenu = function(){
                $(".panel").each(function(index, elem){
                    var elem = $(this);
                    var a = elem.children(".panel-collapse.collapse.in");
                    
                    if( a.length > 0)
                    {
                        a.collapse('hide');
                        return false;
                    }
                });
            }
            
            $scope.getMenuLinkIcon = function(oMenuLink)
            {
                if( oMenuLink.link )
                    return oMenuLink.link;
                else if( oMenuLink.imageLinkOff )
                    return oMenuLink.imageLinkOff;
            }
            
            $scope.hasSubLevel = function(oMenuLink)
            {
                if( oMenuLink && oMenuLink.hasSubLevel == true )
                {
                    return true;
                }
                return false;
            }
            
            
            $scope.initialize = function()
            {
                if( $scope.onInit && typeof $scope.onInit == "function")
                {
                    $scope.onInit();
                }
                
                //$scope.initMenuLinks();
            }
            
            $scope.resetThirdLevel = function(bIsVisible)
            {
                $scope.thirdLevelComboBox.selection = null;
                $scope.thirdLevelComboBox.options = [];
                
                if( bIsVisible != null || bIsVisible != undefined && typeof bIsVisible == "boolean")
                    $scope.isThirdLevelVisible = bIsVisible;
            }
            
            $scope.isSelected = function(option)
            {
                return ($scope.thirdLevelComboBox.selection == option.description)
            }
            
            $scope.updateThirdLevelSelection = function()
            {
                for(var key in $scope.menuItemsByLevel[$scope.MENU_LEVEL_3])
                {
                   if( $scope.menuItemsByLevel[$scope.MENU_LEVEL_3][key].description == $scope.thirdLevelMenuSelectedItemDescription)
                   {
                       $scope.thirdLevelMenuSelectedItem = $scope.menuItemsByLevel[$scope.MENU_LEVEL_3][key];
                       return;
                   }
                }
            }
            
            $scope.setThirdLevelSelection = function(oItem)
            {
                if( $scope.thirdLevelMenuSelectedItem)
                    $scope.thirdLevelMenuSelectedItemDescription = $scope.thirdLevelMenuSelectedItem.description;
            }
            
            
            
            
            $scope.isLastLinkOfRow = function(oRow, oMenuLink)
            {
                return oRow.getlastMenuLink().$$hashKey == oMenuLink.$$hashKey;
            }
            
            $scope.isSecondLevelRowOpen = function(iRowId)
            {
                return iRowId == $scope.secondLevelMenuOpened;
            }
            
            $scope.updateFirstLevelMenuRows = function()
            {
                if( !$scope.menuItemsByLevel[0] || $scope.menuItemsByLevel[0].length == 0)
                {
                    return;
                }                
                
                var ITEM_PER_ROW = 3;
                
                // If we are on small screen the menu position is absolute
                if( $(".sidebar-menu").css("position") == "absolute")
                {
                    ITEM_PER_ROW = 5;
                }
                
                var iCounter = 0;
                var iRowIdToOpen = 0;
                Utils.emptyArray($scope.firstLevelMenuRows);
                
                var iRowCounter = 0;
                var oCurrMenuRow = null;
                for(var key in $scope.menuItemsByLevel[0])
                {
                    if( $scope.firstLevelMenuRows.length == 0)
                    {
                        // Performed only on the first loop
                        $scope.firstLevelMenuRows[iRowCounter] = new MenuRowFirstLevel(false, iRowIdToOpen);
                    }
                    
                    // If the max item per row has been reached
                    // then add a new row which is the container for
                    // the second level menu items
                    if( iCounter == ITEM_PER_ROW)
                    {
                        // Add a row which represent the 'second level container'
                        var oContainerId = iRowIdToOpen;
                        iRowCounter++;
                        $scope.firstLevelMenuRows[iRowCounter] = new MenuRowFirstLevel(true, oContainerId);
                        
                        iCounter = 0;
                        iRowIdToOpen++;
                        
                        // then create new row
                        iRowCounter++;
                        $scope.firstLevelMenuRows[iRowCounter] = new MenuRowFirstLevel(false, iRowIdToOpen);
                    }
                    
                    $scope.firstLevelMenuRows[iRowCounter].addMenuLink($scope.menuItemsByLevel[0][key]);
                    
                    iCounter++;
                }
                
                // Add a final 'second level wrapper'
                var oContainerId = iRowIdToOpen;
                iRowCounter++;
                $scope.firstLevelMenuRows[iRowCounter] = new MenuRowFirstLevel(true, oContainerId);
                
            }
            
            
//            $scope.initMenuLinks = function()
//            {
////                if( $scope.getMenuItems && typeof $scope.getMenuItems == "function")
////                    $scope.getMenuItems();
//            }
            
            $scope.updateMapLinks = function()
            {
                $scope.hasMenuItemBeenUpdated = false;
                if( $scope.menuLevelToUpdate == $scope.MENU_LEVEL_1)
                {
                    $scope.menuItemsByLevel[$scope.MENU_LEVEL_1] = $scope.firstLevelMenuItemsList;                    
                    $scope.updateFirstLevelMenuRows();                    
                    $scope.m_sParentText = $scope.menuLegendSelected;
                    
                }
                else if( $scope.menuLevelToUpdate == $scope.MENU_LEVEL_2)
                {
                    $scope.menuItemsByLevel[$scope.MENU_LEVEL_2] = $scope.secondLevelMenuItemsList;
                    $scope.m_sChildText = $scope.menuLegendSelected;
                }
                else if( $scope.menuLevelToUpdate == $scope.MENU_LEVEL_3)
                {
                    $scope.menuItemsByLevel[$scope.MENU_LEVEL_3] = $scope.thirdLevelMenuItems;
                    $scope.thirdLevelComboBox.options = $scope.menuItemsByLevel[$scope.MENU_LEVEL_3];
                }
            }
            
            
            $scope.isTheSameMenuLink = function(oMenuLink_1, oMenuLink_2)
            {
                if( oMenuLink_1 && oMenuLink_2)
                {
                    if( oMenuLink_1.linkId && oMenuLink_2.linkId)
                        return oMenuLink_1.linkId == oMenuLink_2.linkId;
                    else if( oMenuLink_1.description && oMenuLink_2.description)
                        return oMenuLink_1.description == oMenuLink_2.description;
                }
                else 
                    return false;
            }
            
            $scope.isActive = function(menuItem)
            {
                return $scope.isTheSameMenuLink( $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_1], menuItem);
            }

            $scope.isSubActive = function(menuItem)
            {
                return $scope.isTheSameMenuLink( $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_2], menuItem );
            }
            
            /**
             * Handle the click on a '1st level' menu link
             * @param {type} oMenuLink - The menu link user has clicked on
             * @param {type} iSecondLevelRowIdToOpen - The row ID of the sub-level container which will be opened
             * @return {undefined} Void
             */
            $scope.mainItemClick = function(oMenuLink, iSecondLevelRowIdToOpen)
            {
                console.debug("mainItemClick:", oMenuLink);
                
                $scope.isFirstLevel = true;
                $scope.controller.m_bIsFirstLevel = $scope.isFirstLevel;
                
                if( $scope.isActive(oMenuLink) == true)
                {
                    // Item already selected
                    $scope.secondLevelMenuOpened = -1;
                    $scope.m_bIsSubmenuVisible = false;
                    //$scope.activeMenuItem = null;
                    $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_1] = null;
                    
                    $scope.m_sParentText = "";
                    $scope.m_sChildText = "";
                }
                else
                {
                    // *** Item not selected ***
                    
                    // Open the 2nd level box only if the item has a 2nd level
                    if( $scope.hasSubLevel(oMenuLink) )
                        $scope.secondLevelMenuOpened = iSecondLevelRowIdToOpen;
                    
                    $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_1] = oMenuLink;
                    
                    //$scope.m_bIsSubmenuVisible = $scope.hasSubLevel(oMenuLink);
                    
                    $scope.m_sParentText = oMenuLink.description;
                    $scope.m_sChildText = "";
                    
                    if( oMenuLink[$scope.submenuItemsListVarName] )
                    {
                        $scope.submenuItems = oMenuLink[$scope.submenuItemsListVarName];
                    }
                    else
                        oMenuLink.submenuItems = [];
                }
                
                // reset 3rd level and make it not visible
                $scope.resetThirdLevel(false);
                
                $scope.menuLevelToUpdate = $scope.MENU_LEVEL_2;
                
                // Execute the external 'on click' method 
                if( typeof $scope.onMenutItemClick == "function")
                {
                    //$scope.hasMenuItemBeenUpdated = false;
                    $scope.onMenutItemClick(oMenuLink, $scope.controller);
                    
                    //oMenuLink.selected = $scope.isSubActive(oMenuLink);
                }
            }
            
            /**
             * Handle the click on a '2nd level' menu link
             * @param {type} oMenuLink - The menu link user has clicked on
             * @return {undefined} Void
             */
            $scope.submenuItemClick = function(oMenuLink)
            {
                console.debug("submenuItemClick:", oMenuLink);
                
                $scope.isFirstLevel = false;
                $scope.controller.m_bIsFirstLevel = false;

                if( $scope.isSubActive(oMenuLink) == true)
                {
                    //$scope.activeSubItem = null;
                    $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_2] = null;

                    $scope.m_sParentText = $scope.m_sParentText;
                    $scope.m_sChildText = "";
                    
                    // reset 3rd level and make it not visible
                    $scope.resetThirdLevel(false);
                }
                else
                {
                    $scope.resetThirdLevel();

                    //$scope.activeSubItem = item;
                    $scope.selectedMenuItemByLevel[$scope.MENU_LEVEL_2] = oMenuLink;

                    $scope.m_sParentText = $scope.m_sParentText;

                    $scope.m_sChildText = oMenuLink.description;

                    $scope.isThirdLevelVisible = oMenuLink.hasThirdLevel;
                }
                
                $scope.menuLevelToUpdate = $scope.MENU_LEVEL_3;
                
                // Execute the external 'on click' method 
                if( typeof $scope.onMenutItemClick == "function")
                {
                    $scope.onMenutItemClick(oMenuLink, $scope.controller);
                    
                    oMenuLink.selected = $scope.isSubActive(oMenuLink);
                }
            }
            
            
            /**
             * Handle the selection of a '3rd level' menu link (they are in a 'select' box)
             * @param {type} oSelectedItem - The selected item
             * @return {undefined} Void
             */
            $scope.onThirdLevelSelection = function(oSelectedItem)
            {
                //$scope.thirdLevelMenuSelection = $scope.thirdLevelComboBox.selection;
                $scope.thirdLevelMenuSelection = oSelectedItem;

                if(oSelectedItem && $scope.thirdLevelMenuClick && typeof $scope.thirdLevelMenuClick == "function" )
                {
                    var oItem;
                    for(var key in $scope.thirdLevelComboBox.options)
                    {
                        if( $scope.thirdLevelComboBox.options[key].description == $scope.thirdLevelComboBox.selection)
                        {
                            oItem = $scope.thirdLevelComboBox.options[key];
                            break;
                        }
                    }
                    $scope.thirdLevelMenuClick(oItem, $scope.controller);
                }
            }
            
            
            // Initialize
            $scope.updateMapLinks();
            
//            if( $scope.controller )
//            {
//                if( methodName && typeof methodName == "string")
//                {
//                    var methodName = "getMapThirdLevels";//$scope.getThirdLevelMenuItemsMethodName;
//                    methodName = methodName.replace("()", "");
//                    methodName = methodName.split(".");
//                    methodName = methodName[ methodName.length - 1];
//
//                    if( typeof methodName == "function")
//                        $scope.getThirdLevelMenuItems = $scope.controller[methodName];
//                    else
//                        $scope.getThirdLevelMenuItems = function(){};
//                }
//                else
//                    $scope.getThirdLevelMenuItems = function(){};
//            }
        }
    }
});
