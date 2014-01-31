/**
 * @license AzimuthJS
 * (c) 2012-2013 Matt Priour
 * License: MIT
 */
(function() {
angular.module('az.services').factory('az.services.layersService', function($rootScope) {
    var oLayerService = {
    	m_aoBaseLayers: [],
        m_oDynamicLayer: null,
        m_oSensorsLayer: null,

    	getBaseLayers: function(){
    		return this.m_aoBaseLayers;
    	},
        clearBaseLayers: function () {
            this.m_aoBaseLayers = [];
        },
        addBaseLayer: function(oLayer) {
            this.m_aoBaseLayers.push(oLayer);
        },
        getDynamicLayer: function () {
            return this.m_oDynamicLayer;
        },
        setDynamicLayer: function(oLayer) {
            this.m_oDynamicLayer = oLayer;
        },
        getSensorsLayer: function() {
            return this.m_oSensorsLayer;
        },
        setSensorsLayer: function(oLayer) {
            this.m_oSensorsLayer = oLayer;
        }
    };
    return oLayerService;
  });

})()
