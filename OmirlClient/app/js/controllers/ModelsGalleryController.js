/**
 * Created by Daniele Fiori on 29/06/2015.
 */

var ModelsGalleryController = (function() {
    function ModelsGalleryController($scope, $http)
    {
        this.m_oScope = $scope;
        this.m_oScope.m_oController = this;
        
        //****************************************************************************************
        //* Scope variables
        //****************************************************************************************
        $scope.m_bIsAutoplayEnabled = false;
        $scope.m_iAutoplayDuration_ms = 1000;
        $scope.m_iMaxThumbsCount = 7; // MUST BE AN ODD NUMBER
        $scope.m_iThumbsOnSideCount = Math.floor( $scope.m_iMaxThumbsCount / 2 );
        
        // initial image index
        $scope.m_iCurrentImageIndex = 0;
        
        // Set of Photos
        $scope.photos = [
            {src: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', desc: 'Image 01'},
            {src: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', desc: 'Image 02'},
            {src: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', desc: 'Image 03'},
            {src: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', desc: 'Image 04'},
            {src: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', desc: 'Image 05'},
            {src: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', desc: 'Image 06'},
            {src: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', desc: 'Image 01'},
            {src: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', desc: 'Image 02'},
            {src: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', desc: 'Image 03'},
            {src: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', desc: 'Image 04'},
            {src: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', desc: 'Image 05'},
            {src: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', desc: 'Image 06'},
            {src: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', desc: 'Image 01'},
            {src: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', desc: 'Image 02'},
            {src: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', desc: 'Image 03'},
            {src: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', desc: 'Image 04'},
            {src: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', desc: 'Image 05'},
            {src: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', desc: 'Image 06'},
        ];
        
        
        $scope.json = {
            "model" : "",
            "variable" : "",
            "subVariable" : "",
            "dateRef" : "",
            "images" : $scope.photos
        };
        
        
        $scope.isGalleryReady = false;


        

        //****************************************************************************************
        //* Scope methods
        //****************************************************************************************
        
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
            console.debug("toggleAutoplay clicked");
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
            var url = 'http://93.62.155.217:8080/Omirl/rest/stations/Pluvio';
            $http.get(url)
            .success(function(data, status, headers, config){
                
                // DEBUG(+)
                data = $scope.json;
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
                }, $scope.m_iAutoplayDuration_ms);
            })
            .error(function(data, status, headers, config) {
                console.error("Fail to do GET:", url)
            });
        }
        
        
        
    }

    ModelsGalleryController.$inject = [
        '$scope', '$http'
    ];
    return ModelsGalleryController;
}) ();
