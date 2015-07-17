/**
 * Created by Daniele Fiori on 29/06/2015.
 */

var ModelsGalleryController = (function() {
    function ModelsGalleryController($scope, $http, GalleryService)
    {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        
        //****************************************************************************************
        //* Scope variables
        //****************************************************************************************
        $scope.m_bIsAutoplayEnabled = false;
        $scope.m_iAutoplayDuration_ms = 1000;
        $scope.m_iMaxThumbsCount = 9; // MUST BE AN ODD NUMBER
        $scope.m_iThumbsOnSideCount = Math.floor( $scope.m_iMaxThumbsCount / 2 );
        
        // initial image index
        $scope.m_iCurrentImageIndex = 0;
        
        // Set of Photos
        $scope.photos = [];
        
        
        $scope.isGalleryReady = false;
        $scope.m_bSideBarCollapsed = false;

        //load gallery link
        GalleryService.loadGalleryLink().success(function(data){
            $scope.menuItemsList = data;
        })
        

        //****************************************************************************************
        //* Scope methods
        //****************************************************************************************
        
        $scope.toggleSideBarClicked = function() {

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
        
        $scope.isSideBarCollapsed = function () {
            return $scope.m_bSideBarCollapsed;
        }
        
        
        $scope.stopAutoplay = function()
        {
            $scope.m_bIsAutoplayEnabled = false;
            $scope.goToFirst();
        }
        
        $scope.goToFirst = function()
        {
            $scope.showPhoto(0);
        }
        $scope.goToLast = function()
        {
            $scope.showPhoto( $scope.photos.length - 1);
        }
        
        $scope.toggleAutoplay = function()
        {
            $scope.m_bIsAutoplayEnabled = !$scope.m_bIsAutoplayEnabled;
        }
        
        // if a current image is the same as requested image
        $scope.isActive = function (index) {
            return $scope.m_iCurrentImageIndex === index;
        };

        // show prev image
        $scope.showPrev = function ()
        {
            $scope.m_iCurrentImageIndex = ($scope.m_iCurrentImageIndex > 0) ? --$scope.m_iCurrentImageIndex : $scope.photos.length - 1;
            $scope.updateThumbsVisibility();
        };

        // show next image
        $scope.showNext = function ()
        {
            $scope.m_iCurrentImageIndex = ($scope.m_iCurrentImageIndex < $scope.photos.length - 1) ? ++$scope.m_iCurrentImageIndex : 0;
            $scope.updateThumbsVisibility();
        };

        // show a certain image
        $scope.showPhoto = function (index)
        {
            $scope.m_iCurrentImageIndex = index;
            $scope.updateThumbsVisibility();
        };
        
        $scope.setThumbVisibility = function(index, visibility)
        {
            if( visibility && visibility === true)
                $scope.photos[index].visible = true;
            else
                $scope.photos[index].visible = false;
        }
        
        $scope.isThumbVisible = function(index)
        {
            if( $scope.photos[index].visible )
            {
                return $scope.photos[index].visible;
            }
            else
                return false;
        }
        
        $scope.updateThumbsVisibility = function()
        {
            for(var i = 0;  i < $scope.photos.length; i++)
            {
                
                if( $scope.m_iCurrentImageIndex < $scope.m_iThumbsOnSideCount )
                {
                    if( i < $scope.m_iMaxThumbsCount)
                        $scope.setThumbVisibility(i, true);
                    else
                        $scope.setThumbVisibility(i, false);
                }
                else if( $scope.m_iCurrentImageIndex < ( $scope.photos.length - $scope.m_iThumbsOnSideCount) )
                {
                    if( i >= ( $scope.m_iCurrentImageIndex - $scope.m_iThumbsOnSideCount) 
                            && i <= ( $scope.m_iCurrentImageIndex + $scope.m_iThumbsOnSideCount) )
                        $scope.setThumbVisibility(i, true);
                    else
                        $scope.setThumbVisibility(i, false);
                }
                else
                {
                    if( i >= $scope.photos.length - (($scope.m_iThumbsOnSideCount * 2) + 1) ) 
                        $scope.setThumbVisibility(i, true);
                    else
                        $scope.setThumbVisibility(i, false);
                }
            }
        }
        
        $scope.initGallery = function()
        {
            
            var oController=this;

/*
            //var param = "bo10arTPrec12GH_TCK_Europe";
            var oFakeLink = {
              codeModel: 'bo10ar',
              codeVariable: 'TPrec12GH',
              codeSubVariable: '_TCK_Europe'
            };
            GalleryService.getData(oFakeLink)
            .success(function(data, status, headers, config){
                
                // Get photos and set gallery visible
                $scope.photos = data.images;
                $scope.updateThumbsVisibility();
                
                $scope.isGalleryReady = true;               

                // Set autoplay
                setInterval(function(){
                    if( $scope.m_bIsAutoplayEnabled === true)
                    {
                        console.debug("HERE");
                        $scope.showNext();
                        $scope.$apply();
                    }
                }, $scope.m_iAutoplayDuration_ms);
            })
            .error(function(data, status, headers, config) {
                console.error("Fail to do GET:");
            });
            
            
            
            /*
            // DEBUG(+)
            var data = GalleryService.getDataDEBUG("aaaa");
            // DEBUG(-)

            // Get photos and set gallery visible
            $scope.photos = data.images;
            $scope.updateThumbsVisibility();

            $scope.isGalleryReady = true;               

            // Set autoplay
            setInterval(function(){
                if( $scope.m_bIsAutoplayEnabled === true)
                {
                    console.debug("HERE");
                    $scope.showNext();
                    $scope.$apply();
                }
            }, $scope.m_iAutoplayDuration_ms);*/

        }
        
        
        
    }

    ModelsGalleryController.$inject = [
        '$scope', '$http', 'GalleryService'
    ];
    return ModelsGalleryController;
}) ();
