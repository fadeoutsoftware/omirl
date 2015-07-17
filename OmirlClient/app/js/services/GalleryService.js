/**
 * Created by Daniele Fiori on 30/06/2015
 */

'use strict';
angular.module('omirl.galleryService', ['omirl.ConstantsService']).
    service('GalleryService', ['$http',  'ConstantsService', function ($http, oConstantsService) {
        this.APIURL = oConstantsService.getAPIURL();

        this.m_oHttp = $http;
        this.m_oGalleryLinks = null;
        var oScope = this;

        this.getData = function(codeModel, codeVariable, codeSubVariable) {
            return this.m_oHttp.get(this.APIURL + '/gallery/' + codeModel + codeVariable + codeSubVariable);
        }

        this.loadGalleryLink = function() {

            return this.m_oHttp.get(this.APIURL + '/gallery/gallerylinks/');
        };



        this.getDataDEBUG = function(oGalleryLink)
        {      
            /*
            oGalleryLink.codeModel = "";
            oGalleryLink.codeVariable = "";
            oGalleryLink.codeSubvariable = "";
            */
            
            // Set of Photos
            var photos = [
                {imageLink: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', description: 'Image 01'},
                {imageLink: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', description: 'Image 02'},
                {imageLink: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', description: 'Image 03'},
                {imageLink: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', description: 'Image 04'},
                {imageLink: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', description: 'Image 05'},
                {imageLink: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', description: 'Image 06'},
                {imageLink: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', description: 'Image 01'},
                {imageLink: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', description: 'Image 02'},
                {imageLink: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', description: 'Image 03'},
                {imageLink: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', description: 'Image 04'},
                {imageLink: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', description: 'Image 05'},
                {imageLink: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', description: 'Image 06'},
                {imageLink: 'http://farm9.staticflickr.com/8042/7918423710_e6dd168d7c_b.jpg', description: 'Image 01'},
                {imageLink: 'http://farm9.staticflickr.com/8449/7918424278_4835c85e7a_b.jpg', description: 'Image 02'},
                {imageLink: 'http://farm9.staticflickr.com/8457/7918424412_bb641455c7_b.jpg', description: 'Image 03'},
                {imageLink: 'http://farm9.staticflickr.com/8179/7918424842_c79f7e345c_b.jpg', description: 'Image 04'},
                {imageLink: 'http://farm9.staticflickr.com/8315/7918425138_b739f0df53_b.jpg', description: 'Image 05'},
                {imageLink: 'http://farm9.staticflickr.com/8461/7918425364_fe6753aa75_b.jpg', description: 'Image 06'},
            ];
        
        
            var json = {
                "model" : "",
                "variable" : "",
                "subVariable" : "",
                "dateRef" : "",
                "images" : photos
            };
            
            json = {"images":[{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015063012.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015063015.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015063018.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015063021.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070100.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070103.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070106.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070109.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070112.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070115.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070118.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070121.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070200.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070203.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070206.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070209.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070212.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070215.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070218.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070221.png"},{"description":"","imageLink":"img/gallery/2015/06/30/00/08_bo10ar_TPrec12_GH_TCK_Europe_2015070300.png"}],"model":"Bolam Europe","refDateMin":"0115-07-01T00:06:32","subVarialbe":null,"variable":"12h Total Precipitation"};
            
            
            return json;
        }

        

    }]);

