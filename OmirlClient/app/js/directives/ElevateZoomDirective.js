'use strict';
angular.module('omirl.elevateZoomDirective', []).
    directive('elevateZoom', [  function () {

        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                //Will watch for changes on the attribute
                attrs.$observe('zoomImage',function(){
                    linkElevateZoom();
                })

                function linkElevateZoom(){
                    //Check if its not empty
                    if (!attrs.zoomImage) return;
                    if (attrs.zoomImage.indexOf("nodata") > -1) return;
                    element.attr('data-zoom-image',attrs.zoomImage);
                    $(element).data('zoom-image', attrs.zoomImage);
                    $(element).elevateZoom({
                        zoomType				: "inner",
                        cursor: "crosshair",
                        scrollZoom: true
                    });
                }

                linkElevateZoom();

            }
        };
    }]);

/**
 * Created by s.adamo on 04/08/2015.
 */
