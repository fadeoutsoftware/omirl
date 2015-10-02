
var MenuRowFirstLevel = (function () {
    // Constructor
    function MenuRowFirstLevel(bIsSecondLevelWrapper, iSecondLevelId) {
        this.m_aoMenuLinks = [];
        this.m_bIsSecondLevelWrapper = bIsSecondLevelWrapper;
        this.m_iSecondLevel = iSecondLevelId;
    }
    // Public Methods
    MenuRowFirstLevel.prototype.getMenuLinks = function () { return this.m_aoMenuLinks; };
    MenuRowFirstLevel.prototype.getlastMenuLink = function () { return this.m_aoMenuLinks[this.m_aoMenuLinks.length - 1]; };
    MenuRowFirstLevel.prototype.addMenuLink = function (oMenuLink) {
        this.m_aoMenuLinks.push(oMenuLink);
    };
    MenuRowFirstLevel.prototype.isSecondLevelWrapper = function () { return this.m_bIsSecondLevelWrapper; };
    MenuRowFirstLevel.prototype.isMenuLinkContainer = function () { return !this.m_bIsSecondLevelWrapper; };
    MenuRowFirstLevel.prototype.getSecondLevelId = function () { return this.m_iSecondLevel; };
    
    MenuRowFirstLevel.prototype.getRowIdToOpen = function () { return this.getSecondLevelId(); };
    
    
    
    
    
    return MenuRowFirstLevel;
})();

