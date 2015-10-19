/**
 * Created by Daniele Fiori on 29/06/2015.
 */

var ModelsGalleryController = (function() {
    function ModelsGalleryController($scope, $http, ConstantsService, GalleryService, $translate)
    {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        this.m_oTranslateService = $translate;
        this.m_oConstantsService = ConstantsService;
        this.m_bNowMode = true;
        this.m_oSelectedLink = null;
        this.m_oReferenceDate = new Date();

        //Set Reference date
        if (this.m_oConstantsService.getReferenceDate() != null)
        {
            if (this.m_oConstantsService.getReferenceDate() != "")
            {
                this.m_oReferenceDate = this.m_oConstantsService.getReferenceDate();
                this.m_bNowMode = false;
            }
        }

        //****************************************************************************************
        //* Scope variables
        //****************************************************************************************
        
        $scope.m_bIsAutoplayEnabled = false;
        $scope.m_iAutoplayDuration_ms = 1000;
        $scope.m_iMaxThumbsCount = 9; // MUST BE AN ODD NUMBER
        $scope.m_iThumbsOnSideCount = Math.floor( $scope.m_iMaxThumbsCount / 2 );
        $scope.m_iGalleryTimeIntervalId;



        this.m_oTranslateService('MODELGALLERY_NOIMAGE').then(function(msg){
            $scope.m_sLoadingText = msg;
        });

        
        // initial image index
        $scope.m_iCurrentImageIndex = 0;
        
        // Set of Photos
        $scope.photos = [];
        
        
        $scope.isGalleryReady = false;
        $scope.m_bSideBarCollapsed = false;

        //load gallery link
        GalleryService.loadGalleryLink().success(function(data){
            $scope.menuLinkItemsList = data;
        });
        // DEBUG (+)
        /*
        var json = '[{"active":false,"code":"bo10ar","description":"Sintesi 1","imageLinkOff":"img/wet.png","isActive":false,"location":"/summarytable","sublevelGalleryLink":null},{"active":false,"code":"bo10ac","description":"Sintesi 2","imageLinkOff":"img/rain_drops.png","isActive":false,"location":"/summarytable","sublevelGalleryLink":null}]';
        $scope.menuLinkItemsList = JSON.parse(json);
        
        for( var key in $scope.menuLinkItemsList)
        {
            $scope.menuLinkItemsList[key].sublevelGalleryLink = JSON.parse(json);
        }
        */
        // DEBUG (-)
        
        
        //****************************************************************************************
        //* Scope methods
        //****************************************************************************************
        $scope.$on('$locationChangeStart', function (event, next, current) {
            $scope.clearAutoplayTimeInterval();
            $scope.goToFirst();
        });
        

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
        
        $scope.clearAutoplayTimeInterval = function()
        {
            if( $scope.m_iGalleryTimeIntervalId )
                clearInterval($scope.m_iGalleryTimeIntervalId);
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

        $scope.resetGallery = function(oLink)
        {
            $scope.m_oController.m_oTranslateService('MODELGALLERY_NOIMAGE').then(function(msg){
                $scope.m_sLoadingText = msg;
            });

            $scope.isGalleryReady = false;
        }

        $scope.getGallery = function(oLink)
        {
            //set selected link
            $scope.m_oController.m_oSelectedLink = oLink;

            $scope.m_oController.m_oTranslateService('MODELGALLERY_LOADING').then(function(msg){
                $scope.m_sLoadingText = msg;
            });

            
            GalleryService.getData(oLink.codeParent, oLink.codeVariable, oLink.code)
                .success(function(data, status, headers, config){

                    if (angular.isDefined(data.images))
                    {
                        if (data.images.length == 0)
                        {
                            $scope.m_oController.m_oTranslateService('MODELGALLERY_NODATA').then(function(msg){
                                $scope.m_sLoadingText = msg;
                            });

                            $scope.isGalleryReady = false;

                            return;
                        }
                    }

                    // Get photos and set gallery visible
                    $scope.photos = data.images;

                    // DEBUG (+)
//                    var sUriPrefix = ConstantsService.getAPIURL().replace("rest", "");
//                    for(var key in $scope.photos)
//                    {
//                        
//                        $scope.photos[key].imageLink = sUriPrefix + $scope.photos[key].imageLink;
//                    }
                    // DEBUG (+)
                    
                    $scope.updateThumbsVisibility();

                    $scope.isGalleryReady = true;

                    //clear previous time interval (if any) and set the 1st photo to be shown
                    $scope.clearAutoplayTimeInterval();
                    $scope.goToFirst();
                    // then set autoplay time interval
                    $scope.m_iGalleryTimeIntervalId = setInterval(function(){
                        if( $scope.m_bIsAutoplayEnabled === true)
                        {
                            $scope.showNext();
                            $scope.$apply();
                        }
                    }, $scope.m_iAutoplayDuration_ms);
                })
                .error(function(data, status, headers, config) {
                    console.error("Fail to do GET:");
                });
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

    ModelsGalleryController.prototype.onTimeSet = function (newDate, oldDate) {

        this.m_oConstantsService.setReferenceDate(newDate);
        this.m_bNowMode = false;
        this.stopAutoplay();
        this.getGallery(this.m_oSelectedLink);
        this.goToFirst();

    };


    ModelsGalleryController.prototype.setNow = function () {

        this.m_oConstantsService.setReferenceDate("");
        this.m_bNowMode = true;
        this.m_oReferenceDate = new Date();
        this.stopAutoplay();
        this.getGallery(this.m_oSelectedLink);
        this.goToFirst();
    };

    ModelsGalleryController.$inject = [
        '$scope', '$http', 'ConstantsService', 'GalleryService', '$translate'
    ];
    return ModelsGalleryController;
}) ();
